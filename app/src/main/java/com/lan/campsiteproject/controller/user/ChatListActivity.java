package com.lan.campsiteproject.controller.user;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.lan.campsiteproject.adapter.ConversationAdapter;
import com.lan.campsiteproject.controller.user.ChatActivity;
import com.lan.campsiteproject.R;
import com.lan.campsiteproject.adapter.UserAdapter;
import com.lan.campsiteproject.model.Conversation;
import com.lan.campsiteproject.model.User;
import com.lan.campsiteproject.App;
import java.util.ArrayList;
import java.util.List;

public class ChatListActivity extends AppCompatActivity {
    private static final String TAG = "ChatListActivity";
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private String currentUserId;
    private EditText searchPhone;
    private Button searchButton;
    private RecyclerView searchResults;
    private RecyclerView conversationList;
    private List<User> searchResultUsers = new ArrayList<>();
    private UserAdapter userAdapter;
    private List<Conversation> conversations = new ArrayList<>();
    private ConversationAdapter conversationAdapter;
    private TextView noResults;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);

        mAuth = FirebaseAuth.getInstance();
        db = App.db;
        Log.d(TAG, "mAuth.getCurrentUser(): " + mAuth.getCurrentUser());
        currentUserId = mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getUid() : null;
        Log.d(TAG, "currentUserId: " + currentUserId);

        if (currentUserId == null) {
            Log.e(TAG, "User not logged in");
            finish();
            return;
        }

        searchPhone = findViewById(R.id.search_phone);
        searchResults = findViewById(R.id.search_results);
        conversationList = findViewById(R.id.conversation_list);
        noResults = findViewById(R.id.no_results);

        ProgressBar progressBar = findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);

        searchPhone.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchUsers();
            }
            @Override
            public void afterTextChanged(android.text.Editable s) {}
        });

        searchResults.setLayoutManager(new LinearLayoutManager(this));
        userAdapter = new UserAdapter(searchResultUsers, user -> {
            Intent intent = new Intent(ChatListActivity.this, ChatActivity.class);
            intent.putExtra("other_user_id", user.getFirebaseUid());
            startActivity(intent);
            searchResults.setVisibility(View.GONE);
            searchPhone.setText("");
            searchResultUsers.clear();
            userAdapter.notifyDataSetChanged();
        });
        searchResults.setAdapter(userAdapter);

        conversationList.setLayoutManager(new LinearLayoutManager(this));
        conversationAdapter = new ConversationAdapter(conversations, conv -> {
            Intent intent = new Intent(ChatListActivity.this, ChatActivity.class);
            intent.putExtra("other_user_id", conv.getOtherUserId());
            startActivity(intent);
        });
        conversationList.setAdapter(conversationAdapter);
        loadConversations();
    }

    private void searchUsers() {
        String phoneInput = searchPhone.getText().toString().trim();
        if (phoneInput.length() < 7) {
            searchResults.setVisibility(View.GONE);
            if (searchResultUsers.isEmpty()) {
                noResults.setVisibility(View.VISIBLE);
            } else {
                noResults.setVisibility(View.GONE);
            }
            searchResultUsers.clear();
            userAdapter.notifyDataSetChanged();
            return;
        }
        String normalized = phoneInput.startsWith("0") ? "+84" + phoneInput.substring(1) : phoneInput;

        db.collection("users")
                .whereGreaterThanOrEqualTo("phoneNumber", phoneInput)
                .whereLessThanOrEqualTo("phoneNumber", phoneInput + "\uf8ff")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    searchResultUsers.clear();
                    for (var doc : querySnapshot) {
                        User user = doc.toObject(User.class);
                        if (user.getFirebaseUid() == null) continue;
                        if (user.getFirebaseUid().equals(currentUserId)) {
                            Log.d(TAG, "Skipping current user: " + user.getFirebaseUid());
                            continue;
                        }
                        if (searchResultUsers.stream().noneMatch(u -> u.getFirebaseUid().equals(user.getFirebaseUid()))) {
                            searchResultUsers.add(user);
                        }
                    }
                    if (!phoneInput.equals(normalized)) {
                        db.collection("users")
                                .whereGreaterThanOrEqualTo("phoneNumber", normalized)
                                .whereLessThanOrEqualTo("phoneNumber", normalized + "\uf8ff")
                                .get()
                                .addOnSuccessListener(normalizedSnapshot -> {
                                    for (var doc : normalizedSnapshot) {
                                        User user = doc.toObject(User.class);
                                        if (user.getFirebaseUid() == null) continue;
                                        if (user.getFirebaseUid().equals(currentUserId)) {
                                            Log.d(TAG, "Skipping current user: " + user.getFirebaseUid());
                                            continue;
                                        }
                                        if (searchResultUsers.stream().noneMatch(u -> u.getFirebaseUid().equals(user.getFirebaseUid()))) {
                                            searchResultUsers.add(user);
                                        }
                                    }
                                    userAdapter.notifyDataSetChanged();
                                    searchResults.setVisibility(searchResultUsers.isEmpty() ? View.GONE : View.VISIBLE);
                                });
                    } else {
                        userAdapter.notifyDataSetChanged();
                        searchResults.setVisibility(searchResultUsers.isEmpty() ? View.GONE : View.VISIBLE);
                    }
                })
                .addOnFailureListener(e -> searchResults.setVisibility(View.GONE));
    }
    private void loadConversations() {
        db.collection("chat_metadata").document(currentUserId).collection("conversations")
                .orderBy("lastMessageTime", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .addSnapshotListener((snapshot, e) -> {
                    if (e != null) {
                        Log.e(TAG, "Conversation load error: " + e.getMessage());
                        return;
                    }
                    if (snapshot == null) return;
                    conversations.clear();
                    List<String> userIds = new ArrayList<>();
                    for (var doc : snapshot.getDocuments()) {
                        Conversation conv = new Conversation();
                        conv.setChatId(doc.getId());
                        conv.setOtherUserId(doc.getString("otherUserId"));
                        conv.setLastMessage(doc.getString("lastMessage"));
                        conv.setLastMessageTime(doc.getTimestamp("lastMessageTime") != null
                                ? doc.getTimestamp("lastMessageTime").toDate().getTime() : 0);
                        conversations.add(conv);
                        userIds.add(conv.getOtherUserId());
                    }
                    // Fetch user info for all otherUserIds
                    if (!userIds.isEmpty()) {
                        db.collection("users").whereIn("firebaseUid", userIds)
                                .get()
                                .addOnSuccessListener(usersSnapshot -> {
                                    for (var userDoc : usersSnapshot.getDocuments()) {
                                        String uid = userDoc.getString("firebaseUid");
                                        for (Conversation conv : conversations) {
                                            if (conv.getOtherUserId().equals(uid)) {
                                                conv.setOtherUserName(userDoc.getString("firstName") + " " + userDoc.getString("lastName"));
                                                conv.setOtherUserProfileImageUrl(userDoc.getString("profileImageUrl"));
                                            }
                                        }
                                    }
                                    conversationAdapter.notifyDataSetChanged();
                                });
                    } else {
                        conversationAdapter.notifyDataSetChanged();
                    }
                });
    }
}
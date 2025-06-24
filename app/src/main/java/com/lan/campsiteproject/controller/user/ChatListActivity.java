package com.lan.campsiteproject.controller.user;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.lan.campsiteproject.controller.user.ChatActivity;
import com.lan.campsiteproject.R;
import com.lan.campsiteproject.adapter.UserAdapter;
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
        searchButton = findViewById(R.id.search_button);
        searchResults = findViewById(R.id.search_results);
        conversationList = findViewById(R.id.conversation_list);

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
        // Load conversations (placeholder)
        loadConversations();

        searchButton.setOnClickListener(v -> searchUsers());
    }

    private void searchUsers() {
        String phoneNumber = searchPhone.getText().toString().trim();
        if (phoneNumber.isEmpty()) {
            searchResults.setVisibility(View.GONE);
            return;
        }

        db.collection("users")
                .whereEqualTo("phoneNumber", phoneNumber)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    searchResultUsers.clear();
                    for (var doc : querySnapshot) {
                        User user = doc.toObject(User.class);
                        Log.d("ChatListActivity", "Found user: " + user.getEmail() + ", firebaseUid: " + user.getFirebaseUid());
                        if (!user.getFirebaseUid().equals(currentUserId)) {
                            searchResultUsers.add(user);
                        }
                    }
                    userAdapter.notifyDataSetChanged();
                    searchResults.setVisibility(searchResultUsers.isEmpty() ? View.GONE : View.VISIBLE);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Search error: " + e.getMessage());
                    searchResults.setVisibility(View.GONE);
                });
    }

    private void loadConversations() {
        // Placeholder: Load chat_metadata/{currentUserId}/conversations
        db.collection("chat_metadata").document(currentUserId).collection("conversations")
                .addSnapshotListener((snapshot, e) -> {
                    if (e != null) {
                        Log.e(TAG, "Conversation load error: " + e.getMessage());
                        return;
                    }
                    // Update conversationList RecyclerView (implement adapter)
                });
    }
}
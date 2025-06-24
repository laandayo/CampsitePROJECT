package com.lan.campsiteproject.controller.user;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.Timestamp;
import com.lan.campsiteproject.App;
import com.lan.campsiteproject.R;
import com.lan.campsiteproject.model.Message;
import com.lan.campsiteproject.adapter.MessageAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {
    private static final String TAG = "ChatActivity";
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private String currentUserId;
    private String otherUserId;
    private EditText messageInput;
    private Button sendButton;
    private RecyclerView chatRecycler;
    private List<Message> messages = new ArrayList<>();
    private MessageAdapter messageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mAuth = FirebaseAuth.getInstance();
        db = App.db;
        currentUserId = mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getUid() : null;
        otherUserId = getIntent().getStringExtra("other_user_id");

        if (currentUserId == null || otherUserId == null) {
            Log.e(TAG, "Invalid user IDs");
            finish();
            return;
        }

        chatRecycler = findViewById(R.id.chat_recycler);
        messageInput = findViewById(R.id.message_input);
        sendButton = findViewById(R.id.send_button);

        messageAdapter = new MessageAdapter(messages, currentUserId);
        chatRecycler.setLayoutManager(new LinearLayoutManager(this));
        chatRecycler.setAdapter(messageAdapter);

        sendButton.setOnClickListener(v -> sendMessage());
        loadMessages();
    }

    private void sendMessage() {
        String content = messageInput.getText().toString().trim();
        if (content.isEmpty()) return;

        String chatId = getChatId(currentUserId, otherUserId);
        Message message = new Message(currentUserId, otherUserId, content, Timestamp.now(), "sent");

        db.collection("chats").document(chatId).collection("messages").add(message)
                .addOnSuccessListener(doc -> {
                    message.setMessageId(doc.getId());
                    // No need to add to messages here, will be added by listener
                    messageInput.setText("");
                    updateChatMetadata(chatId, content);
                })
                .addOnFailureListener(e -> Log.e(TAG, "Send error: " + e.getMessage()));
    }

    private void loadMessages() {
        String chatId = getChatId(currentUserId, otherUserId);
        db.collection("chats").document(chatId).collection("messages")
                .orderBy("timestamp")
                .addSnapshotListener((snapshot, e) -> {
                    if (e != null) {
                        Log.e(TAG, "Listen error: " + e.getMessage());
                        return;
                    }
                    if (snapshot != null) {
                        for (DocumentChange dc : snapshot.getDocumentChanges()) {
                            if (dc.getType() == DocumentChange.Type.ADDED) {
                                Message message = dc.getDocument().toObject(Message.class);
                                message.setMessageId(dc.getDocument().getId());
                                messages.add(message);
                            }
                        }
                        messageAdapter.notifyDataSetChanged();
                        chatRecycler.scrollToPosition(messages.size() - 1);
                    }
                });
    }

    private String getChatId(String userId1, String userId2) {
        return userId1.compareTo(userId2) < 0 ? userId1 + "_" + userId2 : userId2 + "_" + userId1;
    }

    private void updateChatMetadata(String chatId, String lastMessage) {
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("otherUserId", otherUserId);
        metadata.put("lastMessage", lastMessage);
        metadata.put("lastMessageTime", Timestamp.now());

        db.collection("chat_metadata").document(currentUserId).collection("conversations")
                .document(chatId).set(metadata);

        metadata.put("otherUserId", currentUserId);
        db.collection("chat_metadata").document(otherUserId).collection("conversations")
                .document(chatId).set(metadata);
    }
}
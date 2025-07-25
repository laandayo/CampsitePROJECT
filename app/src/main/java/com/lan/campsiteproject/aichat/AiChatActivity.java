package com.lan.campsiteproject.aichat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import com.lan.campsiteproject.R;
import com.lan.campsiteproject.aichat.ChatAdapter;
import com.lan.campsiteproject.aichat.ChatMessage;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AiChatActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private EditText editTextMessage;
    private Button buttonSend;
    private ChatAdapter chatAdapter;
    private List<ChatMessage> chatList;

    private static final String OPENAI_API_KEY = "123";
    private static final String ENDPOINT = "https://api.openai.com/v1/chat/completions";
    private final OkHttpClient client = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ai_chat); // 👉 nếu muốn, có thể đổi thành activity_ai_chat.xml

        recyclerView = findViewById(R.id.recyclerView);
        editTextMessage = findViewById(R.id.editTextMessage);
        buttonSend = findViewById(R.id.buttonSend);

        chatList = new ArrayList<>();
        chatAdapter = new ChatAdapter(chatList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(chatAdapter);

        buttonSend.setOnClickListener(view -> {
            String userMessage = editTextMessage.getText().toString().trim();
            if (!userMessage.isEmpty()) {
                addMessage(userMessage, true);
                editTextMessage.setText("");
                callChatGPT(userMessage);
            }
        });
    }

    private void addMessage(String message, boolean isUser) {
        runOnUiThread(() -> {
            chatList.add(new ChatMessage(message, isUser));
            chatAdapter.notifyItemInserted(chatList.size() - 1);
            recyclerView.scrollToPosition(chatList.size() - 1);
        });
    }

    private void callChatGPT(String userMessage) {
        MediaType mediaType = MediaType.get("application/json; charset=utf-8");

        JSONObject json = new JSONObject();
        try {
            json.put("model", "gpt-3.5-turbo");
            JSONArray messages = new JSONArray();
            messages.put(new JSONObject().put("role", "user").put("content", userMessage));
            json.put("messages", messages);
        } catch (Exception e) {
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(mediaType, json.toString());

        Request request = new Request.Builder()
                .url(ENDPOINT)
                .header("Authorization", "Bearer " + OPENAI_API_KEY)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                addMessage("Lỗi kết nối: " + e.getMessage(), false);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    addMessage("Lỗi phản hồi: " + response.message(), false);
                    return;
                }

                try {
                    String responseBody = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseBody);
                    JSONArray choices = jsonObject.getJSONArray("choices");
                    String reply = choices.getJSONObject(0).getJSONObject("message").getString("content");
                    addMessage(reply.trim(), false);
                } catch (Exception e) {
                    addMessage("Lỗi xử lý JSON: " + e.getMessage(), false);
                }
            }
        });
    }
}
package com.lan.campsiteproject;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.lan.campsiteproject.controller.campsite.CartActivity;
import com.lan.campsiteproject.controller.campsite.CartManager;
import com.lan.campsiteproject.controller.campsite.ListCampsiteActivity;

import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final String BASE_URL = "http://10.0.2.2:3000"; // Emulator
    private RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        queue = Volley.newRequestQueue(this);

        getAccounts(); // test kết nối

        // Bước 1: Kiểm tra đơn chưa thanh toán
        checkUnpaidOrderReminder();

        // Bước 2: Yêu cầu quyền thông báo nếu Android 13+
        requestNotificationPermission();
    }

    private void getAccounts() {
        String url = BASE_URL + "/accounts";
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    StringBuilder result = new StringBuilder("Accounts:\n");
                    try {
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject account = response.getJSONObject(i);
                            result.append("ID: ").append(account.getInt("Account_id"))
                                    .append(", Name: ").append(account.getString("first_name"))
                                    .append(" ").append(account.getString("last_name"))
                                    .append(", Email: ").append(account.getString("Gmail"))
                                    .append(", Admin: ").append(account.getBoolean("isAdmin"))
                                    .append("\n");
                        }
                        Log.d(TAG, result.toString());
                        Toast.makeText(MainActivity.this, result.toString(), Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Log.e(TAG, "JSON parsing error: " + e.getMessage());
                        Toast.makeText(MainActivity.this, "Error parsing accounts", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Log.e(TAG, "Error fetching accounts: " + error.getMessage());
                    Toast.makeText(MainActivity.this, "Failed to fetch accounts: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                });

//        Intent intent = new Intent(MainActivity.this, com.lan.campsiteproject.controller.user.ChatActivity.class);
//        intent.putExtra("other_user_id", "some_valid_user_id");
//        startActivity(intent);

        com.google.android.material.floatingactionbutton.FloatingActionButton goToChatButton = findViewById(R.id.goToChatButton);
        goToChatButton.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, ListCampsiteActivity.class));
        });

        queue.add(request);
    }

    private void checkUnpaidOrderReminder() {
        CartManager cartManager = CartManager.getInstance();
        if (cartManager.getSelectedCampsite() != null) {
            showUnpaidNotification();
        }
    }

    private void showUnpaidNotification() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String channelId = "order_channel";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "Order Notification",
                    NotificationManager.IMPORTANCE_HIGH
            );
            notificationManager.createNotificationChannel(channel);
        }

        Intent intent = new Intent(this, CartActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.ic_cart) // cần có icon cart (có thể dùng vector)
                .setContentTitle("Bạn có đơn hàng chưa thanh toán")
                .setContentText("Nhấn để hoàn tất thanh toán trong giỏ hàng")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        notificationManager.notify(1001, builder.build());
    }

    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS}, 101);
            }
        }
    }
}

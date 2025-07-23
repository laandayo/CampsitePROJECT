package com.lan.campsiteproject.controller.orders;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lan.campsiteproject.R;
import com.lan.campsiteproject.adapter.OrderHistoryAdapter;
import com.lan.campsiteproject.model.Order;

import java.io.Serializable;
import java.util.List;

public class OrderHistoryActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private OrderHistoryAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_history);

        recyclerView = findViewById(R.id.recyclerOrderHistory);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Assume bookerId is passed via Intent or retrieved from authentication
        String bookerId = getIntent().getStringExtra("bookerId");
        if (bookerId == null) {
            Toast.makeText(this, "Invalid user", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        OrderManager.getInstance(this).fetchOrders(bookerId, new OrderManager.OrderListCallback() {
            @Override
            public void onSuccess(List<Order> orders) {
                if (orders.isEmpty()) {
                    Toast.makeText(OrderHistoryActivity.this, "Chưa có đơn hàng nào!", Toast.LENGTH_SHORT).show();
                }
                adapter = new OrderHistoryAdapter(OrderHistoryActivity.this, orders, order -> {
                    Intent intent = new Intent(OrderHistoryActivity.this, OrderDetailActivity.class);
                    intent.putExtra("order", (Serializable) order); // Ép kiểu rõ ràng sang Serializable
                    startActivity(intent);
                });
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(OrderHistoryActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
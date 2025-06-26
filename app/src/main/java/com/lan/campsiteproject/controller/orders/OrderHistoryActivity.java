package com.lan.campsiteproject.controller.orders;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lan.campsiteproject.R;
import com.lan.campsiteproject.adapter.OrderHistoryAdapter;
import com.lan.campsiteproject.model.Order;

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

        List<Order> orderList = OrderManager.getInstance().getOrderList();
        if (orderList.isEmpty()) {
            Toast.makeText(this, "Chưa có đơn hàng nào!", Toast.LENGTH_SHORT).show();
        }

        adapter = new OrderHistoryAdapter(this, orderList);
        recyclerView.setAdapter(adapter);
    }
}

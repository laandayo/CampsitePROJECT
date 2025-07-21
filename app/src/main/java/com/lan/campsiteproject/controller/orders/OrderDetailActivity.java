package com.lan.campsiteproject.controller.orders;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.lan.campsiteproject.R;
import com.lan.campsiteproject.adapter.GearInOrderAdapter;
import com.lan.campsiteproject.adapter.OrderHistoryAdapter;
import com.lan.campsiteproject.model.Order;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class OrderDetailActivity extends AppCompatActivity {

    private Order order;
    private TextView txtOrderId, txtCampName, txtBookerName, txtStartDate, txtEndDate, txtStatus, txtTotal;
    private ImageView imgCamp;
    private RecyclerView recyclerGear;
    private Button btnEdit, btnCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        txtOrderId = findViewById(R.id.txtOrderId);
        txtCampName = findViewById(R.id.txtOrderCampName);
        txtBookerName = findViewById(R.id.txtBookerName);
        txtStartDate = findViewById(R.id.txtStartDate);
        txtEndDate = findViewById(R.id.txtEndDate);
        txtStatus = findViewById(R.id.txtOrderStatus);
        txtTotal = findViewById(R.id.txtOrderTotal);
        imgCamp = findViewById(R.id.imgOrderCamp);
        recyclerGear = findViewById(R.id.recyclerGearInOrder);
        btnEdit = findViewById(R.id.btnEditOrder);
        btnCancel = findViewById(R.id.btnCancelOrder);

        order = (Order) getIntent().getSerializableExtra("order");
        if (order == null) {
            Toast.makeText(this, "Invalid order", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

        txtOrderId.setText("Order ID: " + order.getOrderId());
        txtCampName.setText(order.getCampsite().getCampName());
        txtBookerName.setText("Booker: " + order.getBookerName());
        txtStartDate.setText("Start: " + sdf.format(new Date(order.getStartDate().getTime())));
        txtEndDate.setText("End: " + sdf.format(new Date(order.getEndDate().getTime())));
        txtStatus.setText("Status: " + order.getStatus());
        txtTotal.setText("Total: $" + order.getTotalAmount());

        // Load campsite image
        String image = order.getCampsite().getCampImage();
        if (!TextUtils.isEmpty(image)) {
            if (image.endsWith(".jpg") || image.endsWith(".png")) {
                image = image.substring(0, image.lastIndexOf('.'));
            }

            if (image.startsWith("http")) {
                Glide.with(this)
                        .load(image)
                        .placeholder(R.drawable.placeholder)
                        .error(R.drawable.default_camp)
                        .into(imgCamp);
            } else {
                int resId = getResources().getIdentifier(image.trim(), "drawable", getPackageName());
                if (resId != 0) imgCamp.setImageResource(resId);
                else imgCamp.setImageResource(R.drawable.default_camp);
            }
        } else {
            imgCamp.setImageResource(R.drawable.default_camp);
        }

        // Set up gear list
        List<OrderHistoryAdapter.GearEntry> gearEntries = new ArrayList<>();
        for (Map.Entry<com.lan.campsiteproject.model.Gear, Integer> entry : order.getGearMap().entrySet()) {
            gearEntries.add(new OrderHistoryAdapter.GearEntry(entry.getKey(), entry.getValue()));
        }
        GearInOrderAdapter gearAdapter = new GearInOrderAdapter(this, gearEntries);
        recyclerGear.setLayoutManager(new LinearLayoutManager(this));
        recyclerGear.setAdapter(gearAdapter);

        // Handle edit and cancel buttons based on order status
        long currentTime = System.currentTimeMillis();
        if (order.getEndDate().getTime() < currentTime) {
            btnEdit.setEnabled(false);
            btnCancel.setEnabled(false);
            Toast.makeText(this, "Completed order cannot be modified", Toast.LENGTH_SHORT).show();
        } else if (order.getStartDate().getTime() <= currentTime && currentTime <= order.getEndDate().getTime()) {
            btnCancel.setEnabled(false);
            btnEdit.setOnClickListener(v -> startEditActivity());
        } else {
            btnEdit.setOnClickListener(v -> startEditActivity());
            btnCancel.setOnClickListener(v -> cancelOrder());
        }
    }

    private void startEditActivity() {
        // Implement navigation to an edit activity (not provided here)
        Toast.makeText(this, "Edit order: " + order.getOrderId(), Toast.LENGTH_SHORT).show();
    }

    private void cancelOrder() {
        OrderManager.getInstance().cancelOrder(order.getOrderId(), new OrderManager.OrderCallback() {
            @Override
            public void onSuccess() {
                Toast.makeText(OrderDetailActivity.this, "Order cancelled", Toast.LENGTH_SHORT).show();
                txtStatus.setText("Status: Cancelled");
                btnEdit.setEnabled(false);
                btnCancel.setEnabled(false);
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(OrderDetailActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
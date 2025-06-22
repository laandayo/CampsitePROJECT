package com.lan.campsiteproject.controller.user;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.lan.campsiteproject.R;
import com.lan.campsiteproject.controller.user.CartAdapter;
import com.lan.campsiteproject.controller.user.CartManager;
import com.lan.campsiteproject.model.Campsite;
import com.lan.campsiteproject.model.Gear;

import java.util.Map;

public class CartActivity extends AppCompatActivity {
    private TextView totalPriceTextView;
    private EditText edtNumPeople;
    private RecyclerView recyclerView;
    private CartAdapter cartAdapter;
    private Button btnAddMoreGear;
    private CartManager cartManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        cartManager = CartManager.getInstance();

        totalPriceTextView = findViewById(R.id.totalPriceTextView);
        edtNumPeople = findViewById(R.id.edtNumPeople);
        btnAddMoreGear = findViewById(R.id.btnAddMoreGear);

        recyclerView = findViewById(R.id.cartRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        cartAdapter = new CartAdapter(this, cartManager.getGearMap(), cartManager);
        recyclerView.setAdapter(cartAdapter);

        // Số người mặc định = 1
        edtNumPeople.setText(String.valueOf(cartManager.getNumPeople()));
        edtNumPeople.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                try {
                    int num = Integer.parseInt(edtNumPeople.getText().toString());
                    cartManager.setNumPeople(num);
                    updateTotalPrice();
                } catch (Exception ignored) {}
            }
        });

        btnAddMoreGear.setOnClickListener(v -> {
            Intent intent = new Intent(this, GearListActivity.class);
            startActivity(intent);
        });

        updateTotalPrice();
    }

    @Override
    protected void onResume() {
        super.onResume();
        cartAdapter.notifyDataSetChanged();
        updateTotalPrice();
    }

    private void updateTotalPrice() {
        Campsite selectedCampsite = cartManager.getSelectedCampsite();
        if (selectedCampsite != null) {
            int campsitePrice = selectedCampsite.getCampPrice();
            int totalCampsite = campsitePrice * cartManager.getNumPeople();
            int totalGear = 0;
            for (Map.Entry<Gear, Integer> entry : cartManager.getGearMap().entrySet()) {
                totalGear += entry.getKey().getGearPrice() * entry.getValue();
            }

            int total = totalCampsite + totalGear;
            totalPriceTextView.setText("Total: $" + total);
        }
    }
}

package com.lan.campsiteproject.controller.campsite;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.*;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.lan.campsiteproject.R;
import com.lan.campsiteproject.adapter.CartAdapter;
import com.lan.campsiteproject.controller.orders.OrderHistoryActivity;
import com.lan.campsiteproject.controller.orders.OrderManager;
import com.lan.campsiteproject.model.Campsite;
import com.lan.campsiteproject.model.Gear;

import java.util.Map;

public class CartActivity extends AppCompatActivity {

    private CartManager cartManager;
    private CartAdapter cartAdapter;

    private LinearLayout layoutCampsiteCard;
    private TextView txtEmptyMessage, txtCampName, txtCampAddress, txtCampPrice, totalPriceTextView;
    private ImageView imgCamp;
    private EditText edtNumPeople;
    private RecyclerView recyclerView;
    private Button btnAddMoreGear, btnCheckout, btnCancelOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        cartManager = CartManager.getInstance();

        // Binding UI
        layoutCampsiteCard = findViewById(R.id.layoutCampsiteCard);
        txtEmptyMessage = findViewById(R.id.txtEmptyCartMessage);
        imgCamp = findViewById(R.id.imgCartCampsite);
        txtCampName = findViewById(R.id.txtCartCampName);
        txtCampAddress = findViewById(R.id.txtCartCampAddress);
        txtCampPrice = findViewById(R.id.txtCartCampPrice);
        totalPriceTextView = findViewById(R.id.totalPriceTextView);
        edtNumPeople = findViewById(R.id.edtNumPeople);
        recyclerView = findViewById(R.id.cartRecyclerView);
        btnAddMoreGear = findViewById(R.id.btnAddMoreGear);
        btnCheckout = findViewById(R.id.btnCheckout);
        btnCancelOrder = findViewById(R.id.btnCancelOrder);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        setupCampsiteInfo();
        setupListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();
        cartAdapter = new CartAdapter(this, cartManager.getGearMap(), cartManager);
        recyclerView.setAdapter(cartAdapter);
        updateTotalPrice();
    }

    private void setupCampsiteInfo() {
        Campsite selected = cartManager.getSelectedCampsite();

        if (selected != null) {
            layoutCampsiteCard.setVisibility(View.VISIBLE);
            txtEmptyMessage.setVisibility(View.GONE);

            txtCampName.setText(selected.getCampName());
            txtCampAddress.setText("Địa chỉ: " + selected.getCampAddress());
            txtCampPrice.setText("Giá: $" + selected.getCampPrice());

            String image = selected.getCampImage();
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
                    imgCamp.setImageResource(resId != 0 ? resId : R.drawable.default_camp);
                }
            } else {
                imgCamp.setImageResource(R.drawable.default_camp);
            }

            imgCamp.setOnClickListener(v -> {
                Intent i = new Intent(this, CampsiteDetailActivity.class);
                i.putExtra("name", selected.getCampName());
                i.putExtra("price", selected.getCampPrice());
                i.putExtra("address", selected.getCampAddress());
                i.putExtra("description", selected.getCampDescription());
                i.putExtra("image", selected.getCampImage());
                startActivity(i);
            });

            txtCampName.setOnClickListener(v -> imgCamp.performClick());

        } else {
            layoutCampsiteCard.setVisibility(View.GONE);
            txtEmptyMessage.setVisibility(View.VISIBLE);
        }

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
    }

    private void setupListeners() {
        btnAddMoreGear.setOnClickListener(v ->
                startActivity(new Intent(this, GearListActivity.class)));

        btnCheckout.setOnClickListener(v -> showPaymentOptions());

        btnCancelOrder.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Xác nhận huỷ đơn")
                    .setMessage("Bạn có chắc muốn huỷ order này?")
                    .setPositiveButton("Huỷ", (dialog, which) -> {
                        cartManager.clearCart();
                        Toast.makeText(this, "Đã huỷ order", Toast.LENGTH_SHORT).show();
                        recreate();
                    })
                    .setNegativeButton("Không", null)
                    .show();
        });

        findViewById(R.id.btnViewOrderHistory).setOnClickListener(v ->
                startActivity(new Intent(this, OrderHistoryActivity.class))
        );
    }

    private void updateTotalPrice() {
        Campsite camp = cartManager.getSelectedCampsite();
        int numPeople = cartManager.getNumPeople();
        int total = 0;

        if (camp != null) {
            int totalCampsite = camp.getCampPrice() * numPeople;
            int totalGear = 0;
            for (Map.Entry<Gear, Integer> entry : cartManager.getGearMap().entrySet()) {
                totalGear += entry.getKey().getGearPrice() * entry.getValue();
            }
            total = totalCampsite + totalGear;
        }

        totalPriceTextView.setText("Tổng: $" + total);
    }

    private void showPaymentOptions() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Chọn phương thức thanh toán");

        String[] options = {"Paylater", "Paynow (Banking/ZaloPay)"};
        builder.setItems(options, (dialog, which) -> {
            if (which == 0) handlePayLater();
            else handlePayNow();
        });

        builder.setNegativeButton("Hủy", null);
        builder.show();
    }

    private void handlePayLater() {
        Campsite camp = cartManager.getSelectedCampsite();
        Map<Gear, Integer> gearMap = cartManager.getGearMap();
        int numPeople = cartManager.getNumPeople();

        if (camp == null) {
            Toast.makeText(this, "Chưa chọn Campsite!", Toast.LENGTH_SHORT).show();
            return;
        }

        int total = camp.getCampPrice() * numPeople;
        for (Map.Entry<Gear, Integer> entry : gearMap.entrySet()) {
            total += entry.getKey().getGearPrice() * entry.getValue();
        }

        OrderManager.getInstance().addOrder(camp, gearMap, total, "Đang xác nhận thanh toán");
        Toast.makeText(this, "Đã đặt hàng theo hình thức Paylater", Toast.LENGTH_LONG).show();
        cartManager.clearCart();
        finish();
    }

    private void handlePayNow() {
        // Mở module thanh toán khác
        Toast.makeText(this, "Chức năng Paynow đang được phát triển", Toast.LENGTH_SHORT).show();
    }
}

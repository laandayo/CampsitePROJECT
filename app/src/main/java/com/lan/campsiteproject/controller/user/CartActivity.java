package com.lan.campsiteproject.controller.user;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.lan.campsiteproject.R;
import com.lan.campsiteproject.controller.order.OrderHistoryActivity;
import com.lan.campsiteproject.controller.order.OrderManager;
import com.lan.campsiteproject.model.Campsite;
import com.lan.campsiteproject.model.Gear;

import java.util.Map;

public class CartActivity extends AppCompatActivity {
    private TextView totalPriceTextView;
    private EditText edtNumPeople;
    private RecyclerView recyclerView;
    private CartAdapter cartAdapter;
    private Button btnAddMoreGear, btnCheckout;
    private CartManager cartManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        // ✅ Khởi tạo cartManager trước khi dùng
        cartManager = CartManager.getInstance();

        // Campsite UI
        ImageView imgCamp = findViewById(R.id.imgCartCampsite);
        TextView txtName = findViewById(R.id.txtCartCampName);
        TextView txtAddress = findViewById(R.id.txtCartCampAddress);
        TextView txtPrice = findViewById(R.id.txtCartCampPrice);

        Campsite selectedCampsite = cartManager.getSelectedCampsite();
        if (selectedCampsite != null) {
            txtName.setText(selectedCampsite.getCampName());
            txtAddress.setText("Địa chỉ: " + selectedCampsite.getCampAddress());
            txtPrice.setText("Giá: $" + selectedCampsite.getCampPrice());

            Glide.with(this)
                    .load(selectedCampsite.getCampImage())
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.default_camp)
                    .into(imgCamp);
        }

        totalPriceTextView = findViewById(R.id.totalPriceTextView);
        edtNumPeople = findViewById(R.id.edtNumPeople);
        btnAddMoreGear = findViewById(R.id.btnAddMoreGear);
        btnCheckout = findViewById(R.id.btnCheckout);

        recyclerView = findViewById(R.id.cartRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

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

        btnCheckout.setOnClickListener(v -> {
            Toast.makeText(this, "Thanh toán thành công!", Toast.LENGTH_SHORT).show();
            cartManager.clearCart();
            finish();
        });
        btnCheckout.setOnClickListener(v -> {
            showPaymentOptions();
        });
        Button btnViewOrderHistory = findViewById(R.id.btnViewOrderHistory);

        btnViewOrderHistory.setOnClickListener(v -> {
            Intent intent = new Intent(this, OrderHistoryActivity.class);
            startActivity(intent);
        });


    }

    @Override
    protected void onResume() {
        super.onResume();

        // Cập nhật danh sách Gear khi quay lại màn hình Cart
        cartAdapter = new CartAdapter(this, cartManager.getGearMap(), cartManager);
        recyclerView.setAdapter(cartAdapter);

        updateTotalPrice();
    }

    public void updateTotalPrice() {
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
        } else {
            totalPriceTextView.setText("Total: $0");
        }
    }
    private void showPaymentOptions() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Chọn phương thức thanh toán");

        String[] options = {"Paylater", "Paynow (Banking/ZaloPay)"};
        builder.setItems(options, (dialog, which) -> {
            if (which == 0) {
                handlePayLater();
            } else {
                handlePayNow();
            }
        });

        builder.setNegativeButton("Hủy", null);
        builder.show();
    }
    private void handlePayLater() {
        Campsite camp = cartManager.getSelectedCampsite();
        Map<Gear, Integer> gearMap = cartManager.getGearMap();
        int numPeople = cartManager.getNumPeople();
        int total = 0;

        if (camp != null) {
            int totalCampsite = camp.getCampPrice() * numPeople;
            int totalGear = 0;
            for (Map.Entry<Gear, Integer> entry : gearMap.entrySet()) {
                totalGear += entry.getKey().getGearPrice() * entry.getValue();
            }
            total = totalCampsite + totalGear;

            // 🗂️ Lưu đơn hàng vào DB/local/file... ở đây ta log ra để demo
            // TODO: Replace with actual saving logic (API, Room, Firebase,...)
            System.out.println("==> Đơn hàng PAYLATER:");
            System.out.println("Tên campsite: " + camp.getCampName());
            System.out.println("Số người: " + numPeople);
            System.out.println("Tổng tiền: $" + total);
            System.out.println("Danh sách gear:");
            for (Map.Entry<Gear, Integer> entry : gearMap.entrySet()) {
                System.out.println("- " + entry.getKey().getGearName() + " x" + entry.getValue());
            }
            System.out.println("Trạng thái: Đang xác nhận thanh toán");
            System.out.println("Đã order: true");
            OrderManager.getInstance().addOrder(camp, gearMap, total, "Đang xác nhận thanh toán");
            Toast.makeText(this, "Đã đặt hàng theo hình thức Paylater", Toast.LENGTH_LONG).show();
            cartManager.clearCart();
            finish();
        } else {
            Toast.makeText(this, "Chưa chọn Campsite!", Toast.LENGTH_SHORT).show();
        }
    }
    private void handlePayNow() {
//        // Chuyển sang module thanh toán của người khác
//        Intent intent = new Intent(this, PaynowActivity.class); // file khác sẽ xử lý
//        intent.putExtra("amount", totalPriceTextView.getText().toString());
//        startActivity(intent);
    }



}

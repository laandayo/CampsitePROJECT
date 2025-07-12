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
    private CartPriceCalculator priceCalculator;

    // UI Components
    private LinearLayout layoutCampsiteCard;
    private TextView txtEmptyMessage, txtCampName, txtCampAddress, txtCampPrice;
    private ImageView imgCamp;
    private RecyclerView recyclerView;
    private Button btnAddMoreGear, btnCheckout, btnCancelOrder;

    // Price Calculator Components
    private EditText edtNumAdults, edtNumChildren;
    private ImageButton btnIncreaseAdults, btnDecreaseAdults;
    private ImageButton btnIncreaseChildren, btnDecreaseChildren;
    private Button btnSelectDate;
    private TextView txtCampsiteTotal, totalPriceTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        // Initialize managers
        cartManager = CartManager.getInstance();
        cartManager.restoreFromPreferences(this); // Khôi phục dữ liệu giỏ hàng
        priceCalculator = new CartPriceCalculator(this, cartManager);

        // Initialize UI components in correct order
        initializeViews();
        setupRecyclerView();
        setupPriceCalculator();  // Setup price calculator first
        setupCampsiteInfo();     // Then setup campsite info
        setupActionButtons();
    }

    private void initializeViews() {
        // Main layout components
        layoutCampsiteCard = findViewById(R.id.layoutCampsiteCard);
        txtEmptyMessage = findViewById(R.id.txtEmptyCartMessage);
        imgCamp = findViewById(R.id.imgCartCampsite);
        txtCampName = findViewById(R.id.txtCartCampName);
        txtCampAddress = findViewById(R.id.txtCartCampAddress);
        txtCampPrice = findViewById(R.id.txtCartCampPrice);
        recyclerView = findViewById(R.id.cartRecyclerView);
        btnAddMoreGear = findViewById(R.id.btnAddMoreGear);
        btnCheckout = findViewById(R.id.btnCheckout);
        btnCancelOrder = findViewById(R.id.btnCancelOrder);

        // Price calculator components
        edtNumAdults = findViewById(R.id.edtNumAdults);
        edtNumChildren = findViewById(R.id.edtNumChildren);
        btnIncreaseAdults = findViewById(R.id.btnIncreaseAdults);
        btnDecreaseAdults = findViewById(R.id.btnDecreaseAdults);
        btnIncreaseChildren = findViewById(R.id.btnIncreaseChildren);
        btnDecreaseChildren = findViewById(R.id.btnDecreaseChildren);
        btnSelectDate = findViewById(R.id.btnSelectDate);
        txtCampsiteTotal = findViewById(R.id.txtCampsiteTotal);
        totalPriceTextView = findViewById(R.id.totalPriceTextView);
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void setupPriceCalculator() {
        // Check if all required views are initialized
        if (edtNumAdults == null || edtNumChildren == null ||
                btnIncreaseAdults == null || btnDecreaseAdults == null ||
                btnIncreaseChildren == null || btnDecreaseChildren == null ||
                btnSelectDate == null || txtCampsiteTotal == null || totalPriceTextView == null) {

            // Log error or show toast for debugging
            Toast.makeText(this, "Error: Some price calculator views are not initialized", Toast.LENGTH_SHORT).show();
            return;
        }

        // Initialize price calculator with UI components
        priceCalculator.initializeComponents(
                edtNumAdults, edtNumChildren,
                btnIncreaseAdults, btnDecreaseAdults,
                btnIncreaseChildren, btnDecreaseChildren,
                btnSelectDate, txtCampsiteTotal, totalPriceTextView
        );
    }

    private void setupCampsiteInfo() {
        updateCampsiteInfo();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateCartAdapter();
        updateCampsiteInfo();
        refreshPriceCalculation();
    }

    private void updateCartAdapter() {
        if (cartAdapter == null) {
            cartAdapter = new CartAdapter(this, cartManager.getGearMap(), cartManager);
            cartAdapter.setOnTotalPriceChangeListener(this::refreshPriceCalculation);
            recyclerView.setAdapter(cartAdapter);
        } else {
            cartAdapter.updateGearList(cartManager.getGearMap());
            cartAdapter.setOnTotalPriceChangeListener(this::refreshPriceCalculation);
        }
    }

    private void updateCampsiteInfo() {
        Campsite selected = cartManager.getSelectedCampsite();

        if (selected != null) {
            layoutCampsiteCard.setVisibility(View.VISIBLE);

            // Check if empty message card exists before hiding it
            View emptyMessageCard = findViewById(R.id.cardEmptyMessage);
            if (emptyMessageCard != null) {
                emptyMessageCard.setVisibility(View.GONE);
            }

            txtCampName.setText(selected.getCampName());
            txtCampAddress.setText("Địa chỉ: " + selected.getCampAddress());
            txtCampPrice.setText("Giá: $" + selected.getCampPrice() + "/ngày");

            loadCampsiteImage(selected);
            setupCampsiteClickListeners(selected);

            // Only update price calculator if it's properly initialized
            if (priceCalculator != null && isViewsInitialized()) {
                try {
                    priceCalculator.setCampsite(selected);
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Error updating price calculator", Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            layoutCampsiteCard.setVisibility(View.GONE);

            // Check if empty message card exists before showing it
            View emptyMessageCard = findViewById(R.id.cardEmptyMessage);
            if (emptyMessageCard != null) {
                emptyMessageCard.setVisibility(View.VISIBLE);
            }
        }
    }

    private boolean isViewsInitialized() {
        return edtNumAdults != null && edtNumChildren != null &&
                txtCampsiteTotal != null && totalPriceTextView != null;
    }

    private void loadCampsiteImage(Campsite campsite) {
        if (imgCamp == null) return;

        String image = campsite.getCampImage();
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
    }

    private void setupCampsiteClickListeners(Campsite campsite) {
        if (imgCamp == null || txtCampName == null) return;

        View.OnClickListener campsiteClickListener = v -> {
            Intent intent = new Intent(this, CampsiteDetailActivity.class);
            intent.putExtra("name", campsite.getCampName());
            intent.putExtra("price", campsite.getCampPrice());
            intent.putExtra("address", campsite.getCampAddress());
            intent.putExtra("description", campsite.getCampDescription());
            intent.putExtra("image", campsite.getCampImage());
            startActivity(intent);
        };

        imgCamp.setOnClickListener(campsiteClickListener);
        txtCampName.setOnClickListener(campsiteClickListener);
    }

    private void setupActionButtons() {
        // Add more gear button
        if (btnAddMoreGear != null) {
            btnAddMoreGear.setOnClickListener(v -> {
                Intent intent = new Intent(this, GearListActivity.class);
                startActivity(intent);
            });
        }

        // Checkout button
        if (btnCheckout != null) {
            btnCheckout.setOnClickListener(v -> showPaymentOptions());
        }

        // Cancel order button
        if (btnCancelOrder != null) {
            btnCancelOrder.setOnClickListener(v -> {
                new AlertDialog.Builder(this)
                        .setTitle("Xác nhận huỷ đơn")
                        .setMessage("Bạn có chắc muốn huỷ order này?")
                        .setPositiveButton("Huỷ", (dialog, which) -> {
                            cartManager.clearCart(this);
                            Toast.makeText(this, "Đã huỷ order", Toast.LENGTH_SHORT).show();
                            recreate();
                        })
                        .setNegativeButton("Không", null)
                        .show();
            });
        }

        // View order history button
        View orderHistoryButton = findViewById(R.id.btnViewOrderHistory);
        if (orderHistoryButton != null) {
            orderHistoryButton.setOnClickListener(v -> {
                Intent intent = new Intent(this, OrderHistoryActivity.class);
                startActivity(intent);
            });
        }
    }

    private void refreshPriceCalculation() {
        if (priceCalculator != null && isViewsInitialized()) {
            try {
                priceCalculator.refreshPriceCalculation();
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Error refreshing price calculation", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void showPaymentOptions() {
        if (cartManager.getSelectedCampsite() == null) {
            Toast.makeText(this, "Vui lòng chọn campsite trước khi thanh toán!", Toast.LENGTH_SHORT).show();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Chọn phương thức thanh toán");

        String[] options = {"Paylater", "Paynow (VN Pay)"};
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
        Campsite campsite = cartManager.getSelectedCampsite();
        Map<Gear, Integer> gearMap = cartManager.getGearMap();

        if (campsite == null) {
            Toast.makeText(this, "Chưa chọn Campsite!", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            double totalAmount = priceCalculator.getGrandTotal();
            int adults = priceCalculator.getNumberOfAdults();
            int children = priceCalculator.getNumberOfChildren();
            int numberOfDays = priceCalculator.getNumberOfDays();

            String orderDetails = String.format(
                    "Campsite: %s\nSố người lớn: %d\nSố trẻ em: %d\nSố ngày: %d\nTổng tiền: $%.2f",
                    campsite.getCampName(), adults, children, numberOfDays, totalAmount
            );

            OrderManager.getInstance().addOrder(campsite, gearMap, (int) totalAmount, "Đang xác nhận thanh toán");

            Toast.makeText(this, "Đã đặt hàng theo hình thức Paylater\n" + orderDetails, Toast.LENGTH_LONG).show();

            cartManager.clearCart(this);
            finish();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Lỗi khi xử lý thanh toán. Vui lòng thử lại.", Toast.LENGTH_SHORT).show();
        }
    }

    private void handlePayNow() {
        Toast.makeText(this, "Chức năng Paynow đang được phát triển", Toast.LENGTH_SHORT).show();
    }
}
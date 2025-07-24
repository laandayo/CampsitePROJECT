package com.lan.campsiteproject.controller.campsite;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.*;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.lan.campsiteproject.R;
import com.lan.campsiteproject.adapter.CartAdapter;
import com.lan.campsiteproject.controller.orders.OrderHistoryActivity;
import com.lan.campsiteproject.controller.orders.OrderManager;
import com.lan.campsiteproject.model.Campsite;
import com.lan.campsiteproject.model.Gear;
import com.lan.campsiteproject.payment.VNPay;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class CartActivity extends AppCompatActivity {

    private static final String TAG = "CartActivity";
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
        if (edtNumAdults == null || edtNumChildren == null ||
                btnIncreaseAdults == null || btnDecreaseAdults == null ||
                btnIncreaseChildren == null || btnDecreaseChildren == null ||
                btnSelectDate == null || txtCampsiteTotal == null || totalPriceTextView == null) {
            Toast.makeText(this, "Error: Some price calculator views are not initialized", Toast.LENGTH_SHORT).show();
            return;
        }

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

            View emptyMessageCard = findViewById(R.id.cardEmptyMessage);
            if (emptyMessageCard != null) {
                emptyMessageCard.setVisibility(View.GONE);
            }

            txtCampName.setText(selected.getCampName());
            txtCampAddress.setText("Địa chỉ: " + selected.getCampAddress());
            txtCampPrice.setText("Giá: " + selected.getCampPrice() + "vnđ/ngày");

            loadCampsiteImage(selected);
            setupCampsiteClickListeners(selected);

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
        if (btnAddMoreGear != null) {
            btnAddMoreGear.setOnClickListener(v -> {
                Intent intent = new Intent(this, GearListActivity.class);
                startActivity(intent);
            });
        }

        if (btnCheckout != null) {
            btnCheckout.setOnClickListener(v -> showPaymentOptions());
        }

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

        View orderHistoryButton = findViewById(R.id.btnViewOrderHistory);
        if (orderHistoryButton != null) {
            orderHistoryButton.setOnClickListener(v -> {
                Intent intent = new Intent(this, OrderHistoryActivity.class);
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    intent.putExtra("bookerId", user.getUid());
                }
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
        Log.d(TAG, "handlePayLater() called");

        Campsite campsite = cartManager.getSelectedCampsite();
        Map<Gear, Integer> originalGearMap = cartManager.getGearMap();

        if (campsite == null) {
            Log.e(TAG, "Campsite is null");
            Toast.makeText(this, "Chưa chọn Campsite!", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            double totalAmount = priceCalculator.getGrandTotal();
            int adults = priceCalculator.getNumberOfAdults();
            int children = priceCalculator.getNumberOfChildren();
            int numberOfDays = priceCalculator.getNumberOfDays();

            if (totalAmount <= 0) {
                Log.e(TAG, "Invalid total amount: " + totalAmount);
                Toast.makeText(this, "Tổng tiền không hợp lệ!", Toast.LENGTH_SHORT).show();
                return;
            }

            Calendar startCal = priceCalculator.getStartDate();
            Calendar endCal = priceCalculator.getEndDate();
            if (startCal == null || endCal == null) {
                Log.e(TAG, "Start or end date is null");
                Toast.makeText(this, "Ngày thuê không được thiết lập!", Toast.LENGTH_SHORT).show();
                return;
            }
            Timestamp startDate = new Timestamp(startCal.getTimeInMillis());
            Timestamp endDate = new Timestamp(endCal.getTimeInMillis());

            long currentTime = System.currentTimeMillis();
            if (startDate.getTime() < currentTime) {
                Log.e(TAG, "Start date is before current time");
                Toast.makeText(this, "Ngày bắt đầu phải sau ngày hiện tại (" + new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date(currentTime)) + ")!", Toast.LENGTH_SHORT).show();
                return;
            }
            if (endDate.getTime() <= startDate.getTime()) {
                Log.e(TAG, "End date is before or equal to start date");
                Toast.makeText(this, "Ngày kết thúc phải sau ngày bắt đầu!", Toast.LENGTH_SHORT).show();
                return;
            }

            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user == null) {
                Log.e(TAG, "User is not logged in");
                Toast.makeText(this, "Vui lòng đăng nhập để đặt hàng!", Toast.LENGTH_SHORT).show();
                return;
            }
            String bookerId = user.getUid();
            String bookerName = user.getDisplayName() != null ? user.getDisplayName() : "Unknown User";

            // Convert Map<Gear, Integer> to Map<String, Integer>
            Map<String, Integer> gearMap = new HashMap<>();
            for (Map.Entry<Gear, Integer> entry : originalGearMap.entrySet()) {
                gearMap.put(entry.getKey().getGearId(), entry.getValue()); // Use getGearId()
            }

            Log.d(TAG, "Calling OrderManager.addOrder for booker: " + bookerId);
            OrderManager.getInstance(this).addOrder(
                    campsite,
                    gearMap,
                    (int) totalAmount,
                    "Đang xác nhận thanh toán",
                    bookerId,
                    startDate,
                    endDate,
                    false,
                    false,
                    adults + children,
                    campsite.getCampPrice(),
                    bookerName,
                    new OrderManager.OrderCallback() {
                        @Override
                        public void onSuccess() {
                            Log.d(TAG, "Order added successfully");
                            String orderDetails = String.format(
                                    "Campsite: %s\nSố người lớn: %d\nSố trẻ em: %d\nSố ngày: %d\nTổng tiền: vnđ%.1f",
                                    campsite.getCampName(), adults, children, numberOfDays, totalAmount
                            );
                            if (!CartActivity.this.isFinishing()) {
                                runOnUiThread(() -> {
                                    Toast.makeText(CartActivity.this, "Đã đặt hàng theo hình thức Paylater\n" + orderDetails, Toast.LENGTH_LONG).show();
                                    cartManager.clearCart(CartActivity.this);
                                    finish();
                                });
                            } else {
                                Log.w(TAG, "Activity is finishing, cannot show Toast");
                            }
                        }

                        @Override
                        public void onFailure(String error) {
                            Log.e(TAG, "Order failed: " + error);
                            if (!CartActivity.this.isFinishing()) {
                                runOnUiThread(() -> Toast.makeText(CartActivity.this, "Lỗi khi đặt hàng: " + error, Toast.LENGTH_SHORT).show());
                            }
                        }
                    }
            );
        } catch (Exception e) {
            Log.e(TAG, "Payment error: " + e.getMessage(), e);
            if (!CartActivity.this.isFinishing()) {
                Toast.makeText(this, "Lỗi khi xử lý thanh toán: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void handlePayNow() {
        Log.d(TAG, "handlePayNow() called");

        Campsite campsite = cartManager.getSelectedCampsite();
        if (campsite == null) {
            Toast.makeText(this, "Chưa chọn Campsite!", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            double totalAmount = priceCalculator.getGrandTotal();
            if (totalAmount <= 0) {
                Toast.makeText(this, "Tổng tiền không hợp lệ!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Tạo mã đơn hàng tạm thời (dùng timestamp)
            String orderId = "ORDER" + System.currentTimeMillis();

            // Lấy URL thanh toán VNPay
            String paymentUrl = VNPay.getPaymentUrl(orderId, (long) totalAmount);

            // Mở trình duyệt hoặc WebView để thanh toán
            Intent intent = new Intent(Intent.ACTION_VIEW, android.net.Uri.parse(paymentUrl));
            startActivity(intent);

            // Ghi lại orderId tạm để xử lý khi quay lại
            getSharedPreferences("VNPay", MODE_PRIVATE)
                    .edit()
                    .putString("pendingOrderId", orderId)
                    .putLong("pendingTotal", (long) totalAmount)
                    .apply();

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Lỗi khi tạo URL thanh toán: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent); // Cập nhật intent mới để xử lý

        android.net.Uri data = intent.getData();
        if (data != null && data.toString().startsWith("myapp://vnpay_return")) {
            String responseCode = data.getQueryParameter("vnp_ResponseCode");
            if ("00".equals(responseCode)) {
                saveOrderAfterPaynow(); // Thành công
            } else {
                Toast.makeText(this, "Thanh toán thất bại hoặc bị huỷ", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void saveOrderAfterPaynow() {
        Campsite campsite = cartManager.getSelectedCampsite();
        Map<Gear, Integer> originalGearMap = cartManager.getGearMap();

        if (campsite == null) {
            Toast.makeText(this, "Lỗi: Không có campsite được chọn!", Toast.LENGTH_SHORT).show();
            return;
        }

        double totalAmount = priceCalculator.getGrandTotal();
        int adults = priceCalculator.getNumberOfAdults();
        int children = priceCalculator.getNumberOfChildren();
        int numberOfDays = priceCalculator.getNumberOfDays();

        Calendar startCal = priceCalculator.getStartDate();
        Calendar endCal = priceCalculator.getEndDate();

        if (startCal == null || endCal == null) {
            Toast.makeText(this, "Ngày thuê không hợp lệ!", Toast.LENGTH_SHORT).show();
            return;
        }

        Timestamp startDate = new Timestamp(startCal.getTimeInMillis());
        Timestamp endDate = new Timestamp(endCal.getTimeInMillis());

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "Chưa đăng nhập!", Toast.LENGTH_SHORT).show();
            return;
        }

        String bookerId = user.getUid();
        String bookerName = user.getDisplayName() != null ? user.getDisplayName() : "Unknown";

        // Convert Gear map
        Map<String, Integer> gearMap = new HashMap<>();
        for (Map.Entry<Gear, Integer> entry : originalGearMap.entrySet()) {
            gearMap.put(entry.getKey().getGearId(), entry.getValue());
        }

        OrderManager.getInstance(this).addOrder(
                campsite,
                gearMap,
                (int) totalAmount,
                "Đã thanh toán",
                bookerId,
                startDate,
                endDate,
                true,  // approve
                true,  // payment
                adults + children,
                campsite.getCampPrice(),
                bookerName,
                new OrderManager.OrderCallback() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(CartActivity.this, "Thanh toán thành công!", Toast.LENGTH_LONG).show();
                        cartManager.clearCart(CartActivity.this);
                        finish();
                    }

                    @Override
                    public void onFailure(String error) {
                        Toast.makeText(CartActivity.this, "Lỗi lưu đơn: " + error, Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }




}
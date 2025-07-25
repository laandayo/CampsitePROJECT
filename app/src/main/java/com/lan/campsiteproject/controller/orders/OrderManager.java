package com.lan.campsiteproject.controller.orders;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.lan.campsiteproject.model.Campsite;
import com.lan.campsiteproject.model.Order;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class OrderManager {
    private static OrderManager instance;
    private final List<Order> orderList;
    private final FirebaseFirestore db;
    private final Context context;
    private static final String TAG = "OrderManager";

    private OrderManager(Context context) {
        this.context = context.getApplicationContext();
        orderList = new ArrayList<>();
        db = FirebaseFirestore.getInstance();
        FirebaseFirestore.setLoggingEnabled(true);
    }

    public static OrderManager getInstance(Context context) {
        if (instance == null) {
            instance = new OrderManager(context);
        }
        return instance;
    }

    public void addOrder(Campsite campsite, Map<String, Integer> gearMap, int total, String status,
                         String booker, Timestamp startDate, Timestamp endDate, boolean approveStatus,
                         boolean paymentStatus, int quantity, int bookingPrice, String bookerName,
                         OrderCallback callback) {
        Log.d(TAG, "Starting addOrder for booker: " + booker);

        // Kiểm tra kết nối mạng
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork == null || !activeNetwork.isConnected()) {
            Log.e(TAG, "No network connection");
            callback.onFailure("Không có kết nối mạng");
            return;
        }

        // Kiểm tra dữ liệu đầu vào
        if (campsite == null) {
            Log.e(TAG, "Campsite is null");
            callback.onFailure("Campsite không hợp lệ");
            return;
        }
        if (booker == null || booker.isEmpty()) {
            Log.e(TAG, "Booker ID is null or empty");
            callback.onFailure("ID người đặt không hợp lệ");
            return;
        }
        if (startDate == null || endDate == null) {
            Log.e(TAG, "Start or end date is null");
            callback.onFailure("Ngày bắt đầu hoặc kết thúc không hợp lệ");
            return;
        }
        if (total <= 0) {
            Log.e(TAG, "Total amount is invalid: " + total);
            callback.onFailure("Tổng tiền không hợp lệ");
            return;
        }
        if (quantity <= 0) {
            Log.e(TAG, "Quantity is invalid: " + quantity);
            callback.onFailure("Số lượng không hợp lệ");
            return;
        }

        String orderId = UUID.randomUUID().toString();
        Map<String, Object> orderData = new HashMap<>();
        orderData.put("orderId", orderId);
        orderData.put("createdDate", FieldValue.serverTimestamp());
        orderData.put("booker", booker);
        orderData.put("campsite", campsite);
        orderData.put("gearMap", new HashMap<>(gearMap));
        orderData.put("startDate", startDate);
        orderData.put("endDate", endDate);
        orderData.put("approveStatus", approveStatus);
        orderData.put("paymentStatus", paymentStatus);
        orderData.put("quantity", quantity);
        orderData.put("totalAmount", total);
        orderData.put("bookingPrice", bookingPrice);
        orderData.put("bookerName", bookerName);
        orderData.put("status", status);

        Log.d(TAG, "Attempting to save order: " + orderId + ", data: " + orderData.toString());
        db.collection("orders").document(orderId).set(orderData)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Order saved successfully: " + orderId);
                    Order order = new Order(orderId, null, booker, campsite, new HashMap<>(gearMap),
                            startDate, endDate, approveStatus, paymentStatus, quantity, total,
                            bookingPrice, bookerName, status);
                    orderList.add(order);
                    callback.onSuccess();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to save order: " + e.getMessage(), e);
                    callback.onFailure("Lỗi Firestore: " + e.getMessage());
                });
    }

    public void fetchOrders(String bookerId, OrderListCallback callback) {
        Log.d(TAG, "Fetching orders for booker: " + bookerId);
        db.collection("orders").whereEqualTo("booker", bookerId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    orderList.clear();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Order order = document.toObject(Order.class);
                        orderList.add(order);
                    }
                    Log.d(TAG, "Fetched " + orderList.size() + " orders for booker: " + bookerId);
                    callback.onSuccess(orderList);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to fetch orders: " + e.getMessage(), e);
                    callback.onFailure(e.getMessage());
                });
    }

    public void updateOrder(String orderId, Campsite campsite, Map<String, Integer> gearMap,
                            Timestamp startDate, Timestamp endDate, int quantity, int total,
                            OrderCallback callback) {
        Log.d(TAG, "Updating order: " + orderId);
        Order existingOrder = orderList.stream()
                .filter(order -> order.getOrderId().equals(orderId))
                .findFirst()
                .orElse(null);

        if (existingOrder == null) {
            Log.e(TAG, "Order not found: " + orderId);
            callback.onFailure("Order not found");
            return;
        }

        long currentTime = System.currentTimeMillis();
        if (existingOrder.getEndDate().getTime() < currentTime) {
            Log.e(TAG, "Cannot edit completed order: " + orderId);
            callback.onFailure("Cannot edit completed order");
            return;
        }

        existingOrder.setCampsite(campsite);
        existingOrder.setGearMap(new HashMap<>(gearMap));
        existingOrder.setStartDate(startDate);
        existingOrder.setEndDate(endDate);
        existingOrder.setQuantity(quantity);
        existingOrder.setTotalAmount(total);

        Log.d(TAG, "Attempting to update order: " + orderId);
        db.collection("orders").document(orderId).set(existingOrder)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Order updated successfully: " + orderId);
                    callback.onSuccess();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to update order: " + e.getMessage(), e);
                    callback.onFailure(e.getMessage());
                });
    }

    public void cancelOrder(String orderId, OrderCallback callback) {
        Log.d(TAG, "Canceling order: " + orderId);
        Order existingOrder = orderList.stream()
                .filter(order -> order.getOrderId().equals(orderId))
                .findFirst()
                .orElse(null);

        if (existingOrder == null) {
            Log.e(TAG, "Order not found: " + orderId);
            callback.onFailure("Order not found");
            return;
        }

        long currentTime = System.currentTimeMillis();
        if (existingOrder.getEndDate().getTime() < currentTime) {
            Log.e(TAG, "Cannot cancel completed order: " + orderId);
            callback.onFailure("Cannot cancel completed order");
            return;
        }
        if (existingOrder.getStartDate().getTime() <= currentTime &&
                currentTime <= existingOrder.getEndDate().getTime()) {
            Log.e(TAG, "Cannot cancel order during rental period: " + orderId);
            callback.onFailure("Cannot cancel order during rental period");
            return;
        }

        existingOrder.setStatus("Cancelled");
        Log.d(TAG, "Attempting to cancel order: " + orderId);
        db.collection("orders").document(orderId).set(existingOrder)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Order cancelled successfully: " + orderId);
                    callback.onSuccess();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to cancel order: " + e.getMessage(), e);
                    callback.onFailure(e.getMessage());
                });
    }

    public List<Order> getOrderList() {
        return orderList;
    }

    public interface OrderCallback {
        void onSuccess();
        void onFailure(String error);
    }

    public interface OrderListCallback {
        void onSuccess(List<Order> orders);
        void onFailure(String error);
    }
}
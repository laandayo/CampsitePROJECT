package com.lan.campsiteproject.controller.orders;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lan.campsiteproject.model.Campsite;
import com.lan.campsiteproject.model.Gear;
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
    private final DatabaseReference databaseReference;

    private OrderManager() {
        orderList = new ArrayList<>();
        databaseReference = FirebaseDatabase.getInstance().getReference("orders");
    }

    public static OrderManager getInstance() {
        if (instance == null) {
            instance = new OrderManager();
        }
        return instance;
    }

    // Add a new order to Firebase
    public void addOrder(Campsite campsite, Map<Gear, Integer> gearMap, int total, String status,
                         String booker, Timestamp startDate, Timestamp endDate, boolean approveStatus,
                         boolean paymentStatus, int quantity, int bookingPrice, String bookerName,
                         OrderCallback callback) {
        String orderId = UUID.randomUUID().toString();
        Order order = new Order(orderId, new Timestamp(System.currentTimeMillis()), booker, campsite,
                new HashMap<>(gearMap), startDate, endDate, approveStatus, paymentStatus,
                quantity, total, bookingPrice, bookerName, status);

        databaseReference.child(orderId).setValue(order, (error, ref) -> {
            if (error == null) {
                orderList.add(order);
                callback.onSuccess();
            } else {
                callback.onFailure(error.getMessage());
            }
        });
    }

    // Fetch all orders from Firebase
    public void fetchOrders(String bookerId, OrderListCallback callback) {
        databaseReference.orderByChild("booker").equalTo(bookerId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        orderList.clear();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Order order = snapshot.getValue(Order.class);
                            if (order != null) {
                                orderList.add(order);
                            }
                        }
                        callback.onSuccess(orderList);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        callback.onFailure(databaseError.getMessage());
                    }
                });
    }

    // Update an existing order
    public void updateOrder(String orderId, Campsite campsite, Map<Gear, Integer> gearMap,
                            Timestamp startDate, Timestamp endDate, int quantity, int total,
                            OrderCallback callback) {
        Order existingOrder = orderList.stream()
                .filter(order -> order.getOrderId().equals(orderId))
                .findFirst()
                .orElse(null);

        if (existingOrder == null) {
            callback.onFailure("Order not found");
            return;
        }

        // Check if order can be edited
        long currentTime = System.currentTimeMillis();
        if (existingOrder.getEndDate().getTime() < currentTime) {
            callback.onFailure("Cannot edit completed order");
            return;
        }

        existingOrder.setCampsite(campsite);
        existingOrder.setGearMap(new HashMap<>(gearMap));
        existingOrder.setStartDate(startDate);
        existingOrder.setEndDate(endDate);
        existingOrder.setQuantity(quantity);
        existingOrder.setTotalAmount(total);

        databaseReference.child(orderId).setValue(existingOrder, (error, ref) -> {
            if (error == null) {
                callback.onSuccess();
            } else {
                callback.onFailure(error.getMessage());
            }
        });
    }

    // Cancel an order
    public void cancelOrder(String orderId, OrderCallback callback) {
        Order existingOrder = orderList.stream()
                .filter(order -> order.getOrderId().equals(orderId))
                .findFirst()
                .orElse(null);

        if (existingOrder == null) {
            callback.onFailure("Order not found");
            return;
        }

        long currentTime = System.currentTimeMillis();
        if (existingOrder.getEndDate().getTime() < currentTime) {
            callback.onFailure("Cannot cancel completed order");
            return;
        }
        if (existingOrder.getStartDate().getTime() <= currentTime &&
                currentTime <= existingOrder.getEndDate().getTime()) {
            callback.onFailure("Cannot cancel order during rental period");
            return;
        }

        existingOrder.setStatus("Cancelled");
        databaseReference.child(orderId).setValue(existingOrder, (error, ref) -> {
            if (error == null) {
                callback.onSuccess();
            } else {
                callback.onFailure(error.getMessage());
            }
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
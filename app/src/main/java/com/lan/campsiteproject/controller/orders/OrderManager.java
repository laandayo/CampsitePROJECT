package com.lan.campsiteproject.controller.orders;

import com.lan.campsiteproject.model.Campsite;
import com.lan.campsiteproject.model.Gear;
import com.lan.campsiteproject.model.Order;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderManager {
    private static OrderManager instance;
    private final List<Order> orderList;

    private OrderManager() {
        orderList = new ArrayList<>();
    }

    public static OrderManager getInstance() {
        if (instance == null) {
            instance = new OrderManager();
        }
        return instance;
    }

    // ✅ Thêm 1 đơn hàng mới vào danh sách
    public void addOrder(Campsite campsite, Map<Gear, Integer> gearMap, int total, String status) {
        Order order = new Order(campsite, new HashMap<>(gearMap), total, status);
        orderList.add(order);
    }

    // ✅ Trả danh sách đơn hàng (dùng trong OrderHistoryActivity)
    public List<Order> getOrderList() {
        return orderList;
    }
}

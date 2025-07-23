package com.lan.campsiteproject.model;

import com.google.firebase.firestore.PropertyName;
import java.sql.Timestamp;
import java.util.Map;

public class Order {
    private String orderId;
    private Timestamp createdDate;
    private String booker;
    private Campsite campsite;
    private Map<String, Integer> gearMap; // Changed to Map<String, Integer>
    private Timestamp startDate;
    private Timestamp endDate;
    private boolean approveStatus;
    private boolean paymentStatus;
    private int quantity;
    private int totalAmount;
    private int bookingPrice;
    private String bookerName;
    private String status;

    public Order() {} // No-arg constructor for Firestore

    public Order(String orderId, Timestamp createdDate, String booker, Campsite campsite,
                 Map<String, Integer> gearMap, Timestamp startDate, Timestamp endDate,
                 boolean approveStatus, boolean paymentStatus, int quantity, int totalAmount,
                 int bookingPrice, String bookerName, String status) {
        this.orderId = orderId;
        this.createdDate = createdDate;
        this.booker = booker;
        this.campsite = campsite;
        this.gearMap = gearMap;
        this.startDate = startDate;
        this.endDate = endDate;
        this.approveStatus = approveStatus;
        this.paymentStatus = paymentStatus;
        this.quantity = quantity;
        this.totalAmount = totalAmount;
        this.bookingPrice = bookingPrice;
        this.bookerName = bookerName;
        this.status = status;
    }

    @PropertyName("orderId")
    public String getOrderId() { return orderId; }
    @PropertyName("orderId")
    public void setOrderId(String orderId) { this.orderId = orderId; }
    @PropertyName("createdDate")
    public Timestamp getCreatedDate() { return createdDate; }
    @PropertyName("createdDate")
    public void setCreatedDate(Timestamp createdDate) { this.createdDate = createdDate; }
    @PropertyName("booker")
    public String getBooker() { return booker; }
    @PropertyName("booker")
    public void setBooker(String booker) { this.booker = booker; }
    @PropertyName("campsite")
    public Campsite getCampsite() { return campsite; }
    @PropertyName("campsite")
    public void setCampsite(Campsite campsite) { this.campsite = campsite; }
    @PropertyName("gearMap")
    public Map<String, Integer> getGearMap() { return gearMap; }
    @PropertyName("gearMap")
    public void setGearMap(Map<String, Integer> gearMap) { this.gearMap = gearMap; }
    @PropertyName("startDate")
    public Timestamp getStartDate() { return startDate; }
    @PropertyName("startDate")
    public void setStartDate(Timestamp startDate) { this.startDate = startDate; }
    @PropertyName("endDate")
    public Timestamp getEndDate() { return endDate; }
    @PropertyName("endDate")
    public void setEndDate(Timestamp endDate) { this.endDate = endDate; }
    @PropertyName("approveStatus")
    public boolean isApproveStatus() { return approveStatus; }
    @PropertyName("approveStatus")
    public void setApproveStatus(boolean approveStatus) { this.approveStatus = approveStatus; }
    @PropertyName("paymentStatus")
    public boolean isPaymentStatus() { return paymentStatus; }
    @PropertyName("paymentStatus")
    public void setPaymentStatus(boolean paymentStatus) { this.paymentStatus = paymentStatus; }
    @PropertyName("quantity")
    public int getQuantity() { return quantity; }
    @PropertyName("quantity")
    public void setQuantity(int quantity) { this.quantity = quantity; }
    @PropertyName("totalAmount")
    public int getTotalAmount() { return totalAmount; }
    @PropertyName("totalAmount")
    public void setTotalAmount(int totalAmount) { this.totalAmount = totalAmount; }
    @PropertyName("bookingPrice")
    public int getBookingPrice() { return bookingPrice; }
    @PropertyName("bookingPrice")
    public void setBookingPrice(int bookingPrice) { this.bookingPrice = bookingPrice; }
    @PropertyName("bookerName")
    public String getBookerName() { return bookerName; }
    @PropertyName("bookerName")
    public void setBookerName(String bookerName) { this.bookerName = bookerName; }
    @PropertyName("status")
    public String getStatus() { return status; }
    @PropertyName("status")
    public void setStatus(String status) { this.status = status; }
}
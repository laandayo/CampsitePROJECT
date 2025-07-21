package com.lan.campsiteproject.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Map;
import java.util.Objects;

public class Order implements Serializable {
    @SerializedName("orderId")
    @Expose
    private String orderId;
    @SerializedName("timeStamp")
    @Expose
    private Timestamp timeStamp;
    @SerializedName("booker")
    @Expose
    private String booker;
    @SerializedName("campsite")
    @Expose
    private Campsite campsite;
    @SerializedName("gearMap")
    @Expose
    private Map<Gear, Integer> gearMap;
    @SerializedName("startDate")
    @Expose
    private Timestamp startDate;
    @SerializedName("endDate")
    @Expose
    private Timestamp endDate;
    @SerializedName("approveStatus")
    @Expose
    private boolean approveStatus;
    @SerializedName("paymentStatus")
    @Expose
    private boolean paymentStatus;
    @SerializedName("quantity")
    @Expose
    private int quantity;
    @SerializedName("totalAmount")
    @Expose
    private int totalAmount;
    @SerializedName("bookingPrice")
    @Expose
    private int bookingPrice;
    @SerializedName("bookerName")
    @Expose
    private String bookerName;
    @SerializedName("status")
    @Expose
    private String status;

    public Order() {
    }

    public Order(String orderId, Timestamp timeStamp, String booker, Campsite campsite, Map<Gear, Integer> gearMap,
                 Timestamp startDate, Timestamp endDate, boolean approveStatus, boolean paymentStatus,
                 int quantity, int totalAmount, int bookingPrice, String bookerName, String status) {
        this.orderId = orderId;
        this.timeStamp = timeStamp;
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

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public Timestamp getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Timestamp timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getBooker() {
        return booker;
    }

    public void setBooker(String booker) {
        this.booker = booker;
    }

    public Campsite getCampsite() {
        return campsite;
    }

    public void setCampsite(Campsite campsite) {
        this.campsite = campsite;
    }

    public Map<Gear, Integer> getGearMap() {
        return gearMap;
    }

    public void setGearMap(Map<Gear, Integer> gearMap) {
        this.gearMap = gearMap;
    }

    public Timestamp getStartDate() {
        return startDate;
    }

    public void setStartDate(Timestamp startDate) {
        this.startDate = startDate;
    }

    public Timestamp getEndDate() {
        return endDate;
    }

    public void setEndDate(Timestamp endDate) {
        this.endDate = endDate;
    }

    public boolean isApproveStatus() {
        return approveStatus;
    }

    public void setApproveStatus(boolean approveStatus) {
        this.approveStatus = approveStatus;
    }

    public boolean isPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(boolean paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(int totalAmount) {
        this.totalAmount = totalAmount;
    }

    public int getBookingPrice() {
        return bookingPrice;
    }

    public void setBookingPrice(int bookingPrice) {
        this.bookingPrice = bookingPrice;
    }

    public String getBookerName() {
        return bookerName;
    }

    public void setBookerName(String bookerName) {
        this.bookerName = bookerName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return orderId != null && orderId.equals(order.orderId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(orderId);
    }
}
package com.lan.campsiteproject.model;

public class CartGearItem {
    private Gear gear;
    private int quantity;

    public CartGearItem(Gear gear, int quantity) {
        this.gear = gear;
        this.quantity = quantity;
    }

    public Gear getGear() {
        return gear;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}

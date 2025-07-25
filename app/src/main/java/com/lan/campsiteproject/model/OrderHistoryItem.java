package com.lan.campsiteproject.model;

public class OrderHistoryItem {
    public static final int TYPE_CAMPSITE = 0;
    public static final int TYPE_GEAR = 1;

    private int type;
    private Campsite campsite;
    private Gear gear;
    private int gearQuantity;

    public OrderHistoryItem(Campsite campsite) {
        this.type = TYPE_CAMPSITE;
        this.campsite = campsite;
    }

    public OrderHistoryItem(Gear gear, int quantity) {
        this.type = TYPE_GEAR;
        this.gear = gear;
        this.gearQuantity = quantity;
    }

    public int getType() { return type; }
    public Campsite getCampsite() { return campsite; }
    public Gear getGear() { return gear; }
    public int getGearQuantity() { return gearQuantity; }
}
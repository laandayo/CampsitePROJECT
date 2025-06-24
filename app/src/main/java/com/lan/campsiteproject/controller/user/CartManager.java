package com.lan.campsiteproject.controller.user;


import com.lan.campsiteproject.model.Campsite;
import com.lan.campsiteproject.model.Gear;

import java.util.HashMap;
import java.util.Map;

public class CartManager {
    private static CartManager instance;
    private Campsite selectedCampsite;
    private Map<Gear, Integer> gearMap;
    private int numPeople;

    private CartManager() {
        gearMap = new HashMap<>();
        numPeople = 1;
    }

    public static CartManager getInstance() {
        if (instance == null) instance = new CartManager();
        return instance;
    }

    public void addCampsite(Campsite campsite) {
        this.selectedCampsite = campsite;
    }

    public Campsite getSelectedCampsite() {
        return selectedCampsite;
    }

    public void setNumPeople(int num) {
        this.numPeople = Math.max(1, num);
    }

    public int getNumPeople() {
        return numPeople;
    }

    public void addGear(Gear gear) {
        gearMap.put(gear, gearMap.getOrDefault(gear, 0) + 1);
    }

    public void removeGear(Gear gear) {
        gearMap.remove(gear);
    }

    public Map<Gear, Integer> getGearMap() {
        return gearMap;
    }

    public void updateGearQuantity(Gear gear, int qty) {
        if (qty <= 0) gearMap.remove(gear);
        else gearMap.put(gear, qty);
    }

    public void clearCart() {
        selectedCampsite = null;
        gearMap.clear();
        numPeople = 1;
    }
    public int getCampsiteQuantity() {
        return selectedCampsite != null ? 1 : 0;
    }

}

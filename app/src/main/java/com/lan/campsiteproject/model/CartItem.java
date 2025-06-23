package com.lan.campsiteproject.model;

public class CartItem {
    private Campsite campsite;
    private int numberOfPeople;

    public CartItem(Campsite campsite, int numberOfPeople) {
        this.campsite = campsite;
        this.numberOfPeople = numberOfPeople;
    }

    public Campsite getCampsite() {
        return campsite;
    }

    public int getNumberOfPeople() {
        return numberOfPeople;
    }

    public void setNumberOfPeople(int numberOfPeople) {
        this.numberOfPeople = numberOfPeople;
    }
}

package com.lan.campsiteproject.model;


public class Campsite {
    private String campId;
    private int campPrice;
    private String campOwner;
    private String campAddress;
    private String campName;
    private String campDescription;
    private String campImage;
    private boolean campStatus;
    private int quantity;
    private String campsiteOwnerName;
    
    public Campsite (){
        
    }

    public Campsite(String campId, int quantity) {
        this.campId = campId;
        this.quantity = quantity;
    }

    public Campsite(String campId, int campPrice, String campOwner, String campAddress, String campName, String campDecription,String campImage, boolean campStatus, String campsiteOwnerName) {
        this.campId = campId;
        this.campPrice = campPrice;
        this.campOwner = campOwner;
        this.campAddress = campAddress;
        this.campName = campName;
        this.campDescription = campDecription;
        this.campImage= campImage;
        this.campStatus = campStatus;
        this.campsiteOwnerName = campsiteOwnerName;
    }

    public String getCampId() {
        return campId;
    }

    public void setCampId(String campId) {
        this.campId = campId;
    }

    public int getCampPrice() {
        return campPrice;
    }

    public void setCampPrice(int campPrice) {
        this.campPrice = campPrice;
    }

    public String getCampOwner() { return campOwner; }

    public void setCampOwner(String campOwner) { this.campOwner = campOwner; }

    public String getCampAddress() {
        return campAddress;
    }

    public void setCampAddress(String campAddress) {
        this.campAddress = campAddress;
    }

    public String getCampName() {
        return campName;
    }

    public void setCampName(String campName) {
        this.campName = campName;
    }

    public String getCampDescription() {
        return campDescription;
    }

    public void setCampDescription(String campDecription) {
        this.campDescription = campDecription;
    }

    public String getCampImage() {
        return campImage;
    }

    public void setCampImage(String campImage) {
        this.campImage = campImage;
    }

    public boolean isCampStatus() {
        return campStatus;
    }

    public void setCampStatus(boolean campStatus) {
        this.campStatus = campStatus;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getCampsiteOwnerName() {
        return campsiteOwnerName;
    }

    public void setCampsiteOwnerName(String campsiteOwnerName) {
        this.campsiteOwnerName = campsiteOwnerName;
    }
}

package com.lan.campsiteproject.model;


public class Gear {
    private String gearId;
    private int gearPrice;
    private String gearOwner;
    private String gearName;
    private String gearDescription;
    private String gearImage;
    
    public Gear(){
        
    }

    public Gear(String gearId, int gearPrice, String gearOwner, String gearName, String gearDescription, String gearImage) {
        this.gearId = gearId;
        this.gearPrice = gearPrice;
        this.gearOwner = gearOwner;
        this.gearName = gearName;
        this.gearDescription = gearDescription;
        this.gearImage = gearImage;
    }

    public String getGearId() {
        return gearId;
    }

    public void setGearId(String gearId) {
        this.gearId = gearId;
    }

    public int getGearPrice() {
        return gearPrice;
    }

    public void setGearPrice(int gearPrice) {
        this.gearPrice = gearPrice;
    }

    public String getGearOwner() {
        return gearOwner;
    }

    public void setGearOwner(String gearOwner) {
        this.gearOwner = gearOwner;
    }

    public String getGearName() {
        return gearName;
    }

    public void setGearName(String gearName) {
        this.gearName = gearName;
    }

    public String getGearDescription() {
        return gearDescription;
    }

    public void setGearDescription(String gearDescription) {
        this.gearDescription = gearDescription;
    }

    public String getGearImage() {
        return gearImage;
    }

    public void setGearImage(String gearImage) {
        this.gearImage = gearImage;
    }

    
    
}

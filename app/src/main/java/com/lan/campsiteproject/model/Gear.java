package com.lan.campsiteproject.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Objects;

public class Gear implements Serializable {
    @SerializedName("gearId")
    @Expose
    private String gearId;
    @SerializedName("gearPrice")
    @Expose
    private int gearPrice;
    @SerializedName("gearOwner")
    @Expose
    private String gearOwner;
    @SerializedName("gearName")
    @Expose
    private String gearName;
    @SerializedName("gearDescription")
    @Expose
    private String gearDescription;
    @SerializedName("gearImage")
    @Expose
    private String gearImage;

    public Gear() {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Gear gear = (Gear) o;
        return gearId != null && gearId.equals(gear.gearId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(gearId);
    }
}
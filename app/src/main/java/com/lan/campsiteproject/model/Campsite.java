package com.lan.campsiteproject.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Objects;

public class Campsite implements Serializable {
    @SerializedName("campId")
    @Expose
    private String campId;
    @SerializedName("campPrice")
    @Expose
    private int campPrice;
    @SerializedName("campOwner")
    @Expose
    private String campOwner;
    @SerializedName("campAddress")
    @Expose
    private String campAddress;
    @SerializedName("campName")
    @Expose
    private String campName;
    @SerializedName("campDescription")
    @Expose
    private String campDescription;
    @SerializedName("campImage")
    @Expose
    private String campImage;
    @SerializedName("campStatus")
    @Expose
    private boolean campStatus;
    @SerializedName("quantity")
    @Expose
    private int quantity;
    @SerializedName("campsiteOwnerName")
    @Expose
    private String campsiteOwnerName;
    @SerializedName("limite")
    @Expose
    private int limite;

    public Campsite() {
    }

    public Campsite(String campId, int quantity) {
        this.campId = campId;
        this.quantity = quantity;
    }

    public Campsite(String campId, int campPrice, String campOwner, String campAddress, String campName,
                    String campDescription, String campImage, boolean campStatus, String campsiteOwnerName) {
        this.campId = campId;
        this.campPrice = campPrice;
        this.campOwner = campOwner;
        this.campAddress = campAddress;
        this.campName = campName;
        this.campDescription = campDescription;
        this.campImage = campImage;
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

    public String getCampOwner() {
        return campOwner;
    }

    public void setCampOwner(String campOwner) {
        this.campOwner = campOwner;
    }

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

    public void setCampDescription(String campDescription) {
        this.campDescription = campDescription;
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

    public int getLimite() {
        return limite;
    }

    public void setLimite(int limite) {
        this.limite = limite;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Campsite campsite = (Campsite) o;
        return campId != null && campId.equals(campsite.campId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(campId);
    }
}
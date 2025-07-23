package com.lan.campsiteproject.model;

import com.google.firebase.firestore.PropertyName;
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

    @PropertyName("campId")
    public String getCampId() {
        return campId;
    }

    @PropertyName("campId")
    public void setCampId(String campId) {
        this.campId = campId;
    }

    @PropertyName("campPrice")
    public int getCampPrice() {
        return campPrice;
    }

    @PropertyName("campPrice")
    public void setCampPrice(int campPrice) {
        this.campPrice = campPrice;
    }

    @PropertyName("campOwner")
    public String getCampOwner() {
        return campOwner;
    }

    @PropertyName("campOwner")
    public void setCampOwner(String campOwner) {
        this.campOwner = campOwner;
    }

    @PropertyName("campAddress")
    public String getCampAddress() {
        return campAddress;
    }

    @PropertyName("campAddress")
    public void setCampAddress(String campAddress) {
        this.campAddress = campAddress;
    }

    @PropertyName("campName")
    public String getCampName() {
        return campName;
    }

    @PropertyName("campName")
    public void setCampName(String campName) {
        this.campName = campName;
    }

    @PropertyName("campDescription")
    public String getCampDescription() {
        return campDescription;
    }

    @PropertyName("campDescription")
    public void setCampDescription(String campDescription) {
        this.campDescription = campDescription;
    }

    @PropertyName("campImage")
    public String getCampImage() {
        return campImage;
    }

    @PropertyName("campImage")
    public void setCampImage(String campImage) {
        this.campImage = campImage;
    }

    @PropertyName("campStatus")
    public boolean isCampStatus() {
        return campStatus;
    }

    @PropertyName("campStatus")
    public void setCampStatus(boolean campStatus) {
        this.campStatus = campStatus;
    }

    @PropertyName("quantity")
    public int getQuantity() {
        return quantity;
    }

    @PropertyName("quantity")
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @PropertyName("campsiteOwnerName")
    public String getCampsiteOwnerName() {
        return campsiteOwnerName;
    }

    @PropertyName("campsiteOwnerName")
    public void setCampsiteOwnerName(String campsiteOwnerName) {
        this.campsiteOwnerName = campsiteOwnerName;
    }

    @PropertyName("limite")
    public int getLimite() {
        return limite;
    }

    @PropertyName("limite")
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
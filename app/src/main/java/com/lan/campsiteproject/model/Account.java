package com.lan.campsiteproject.model;

public class Account {
    public int accountId;
    public String firstName;
    public String lastName;
    public String gmail;
    public boolean isAdmin;

    public Account(int accountId, String firstName, String lastName, String gmail, boolean isAdmin) {
        this.accountId = accountId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gmail = gmail;
        this.isAdmin = isAdmin;
    }
}
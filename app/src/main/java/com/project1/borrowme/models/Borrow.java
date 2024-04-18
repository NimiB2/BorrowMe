package com.project1.borrowme.models;

import java.util.ArrayList;
import java.util.UUID;

public class Borrow {
    private String id;
    private String senderId;
    private boolean OpenBorrow;
    private boolean borrowComplete;
    private String itemName;
    private String description;
    private ArrayList<String> categories = new ArrayList<>();
    private int radiusKm;
    private int numOfSending;
    private int numOfAnswers;
    private double lat;
    private double lon;
    private String senderName;

    public Borrow() {
        this.id = generateUniqueId();
        this.OpenBorrow = true;
        this.borrowComplete = false;
        this.numOfSending = 0;
        this.numOfAnswers = 0;

    }


    public Borrow(String senderName, String senderId, String itemName, String description, ArrayList<String> categories, int radiusKm, double lat, double lon) {
        this.id = generateUniqueId();
        this.OpenBorrow = true;
        this.borrowComplete = false;
        this.numOfSending = 0;
        this.numOfAnswers = 0;


        this.senderName = senderName;
        this.senderId = senderId;
        this.itemName = itemName;
        this.description = description;
        this.categories = categories;
        this.radiusKm = radiusKm;
        this.lat = lat;
        this.lon = lon;
    }

    private static String generateUniqueId() {
        return UUID.randomUUID().toString();
    }

    public String getId() {
        return id;
    }

    public Borrow setId(String id) {
        this.id = id;
        return this;
    }

    public String getSenderId() {
        return senderId;
    }

    public Borrow setSenderId(String senderId) {
        this.senderId = senderId;
        return this;
    }

    public boolean getOpenBorrow() {
        return OpenBorrow;
    }

    public Borrow setOpenBorrow(boolean openBorrow) {
        OpenBorrow = openBorrow;
        return this;
    }

    public boolean getBorrowComplete() {
        return borrowComplete;
    }

    public Borrow setBorrowComplete(boolean borrowComplete) {
        this.borrowComplete = borrowComplete;
        return this;
    }

    public String getItemName() {
        return itemName;
    }

    public Borrow setItemName(String itemName) {
        this.itemName = itemName;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public Borrow setDescription(String description) {
        this.description = description;
        return this;
    }

    public ArrayList<String> getCategories() {
        return categories;
    }

    public Borrow setCategories(ArrayList<String> categories) {
        this.categories = categories;
        return this;
    }

    public int getRadiusKm() {
        return radiusKm;
    }

    public Borrow setRadiusKm(int radiusKm) {
        this.radiusKm = radiusKm;
        return this;
    }

    public int getNumOfSending() {
        return numOfSending;
    }

    public Borrow setNumOfSending(int numOfSending) {
        this.numOfSending = numOfSending;
        return this;
    }

    public int getNumOfAnswers() {
        return numOfAnswers;
    }

    public Borrow setNumOfAnswers(int numOfAnswers) {
        this.numOfAnswers = numOfAnswers;
        return this;
    }

    public double getLat() {
        return lat;
    }

    public Borrow setLat(double lat) {
        this.lat = lat;
        return this;
    }

    public double getLon() {
        return lon;
    }

    public Borrow setLon(double lon) {
        this.lon = lon;
        return this;
    }

    public String getSenderName() {
        return senderName;
    }

    public Borrow setSenderName(String senderName) {
        this.senderName = senderName;
        return this;
    }

    public void updateNumOfSending() {
        this.numOfSending++;
    }

    public void updateNumOfAnswers() {
        this.numOfAnswers++;
    }

    public boolean checkForClosed() {

        if (this.getBorrowComplete()) {
            setOpenBorrow(false);
            return true;

        } else if (this.numOfAnswers == this.numOfSending) {
            setOpenBorrow(false);
            return true;
        }

        return false;
    }

}
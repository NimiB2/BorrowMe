package com.project1.borrowme.models;

import android.location.Location;

import com.project1.borrowme.adpters.UserAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Borrow {
    private String id;
    private UserAdapter senderUser;
    private boolean isOpenBorrow;
    private boolean borrowComplete;
    private String itemName;
    private String description;
    private ArrayList<String> categories =new ArrayList<>();;
    private int distance;
    private int numOfSending;
    private int numOfAnswers;
    private double lat;
    private double lon;

    public Borrow() {
        this.id = generateUniqueId();
        this.isOpenBorrow = false;
        this.borrowComplete = false;
        this.numOfSending = 0;
        this.numOfAnswers = 0;
    }


    public Borrow(UserAdapter senderUser, String itemName, String description, ArrayList<String> categories, int distance, double lat, double lon) {
        this.id = generateUniqueId();
        this.isOpenBorrow = false;
        this.borrowComplete = false;
        this.numOfSending = 0;
        this.numOfAnswers = 0;

        this.senderUser = senderUser;
        this.itemName = itemName;
        this.description = description;
        this.categories = categories;
        this.distance = distance;
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

    public UserAdapter getSenderUser() {
        return senderUser;
    }

    public Borrow setSenderUser(UserAdapter senderUser) {
        this.senderUser = senderUser;
        return this;
    }

    public boolean isOpenBorrow() {
        return isOpenBorrow;
    }

    public Borrow setOpenBorrow(boolean openBorrow) {
        isOpenBorrow = openBorrow;
        return this;
    }

    public boolean isBorrowComplete() {
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

    public int getDistance() {
        return distance;
    }

    public Borrow setDistance(int distance) {
        this.distance = distance;
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
}

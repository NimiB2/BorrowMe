package com.project1.borrowme.models;

import java.util.Map;

public class TheUser {
    private static volatile TheUser instance;
    private String uid;
    private UserDetails userDetails;
   private Map<String,Borrow> borrowMap;
    private Map<String, ReceivedBorrow> receivedBorrowMap;
    private Map<String, ReceivedBorrow> history;
    private Map<String, ReceivedBorrow> massages;

    private TheUser() {
        userDetails = new UserDetails();
    }

    public static TheUser getInstance() {
        if (instance == null) {
            synchronized (TheUser.class) {
                if (instance == null) {
                    instance = new TheUser();
                }
            }
        }
        return instance;
    }

    public String getUid() {
        return uid;
    }

    public TheUser setUid(String uid) {
        this.uid = uid;
        return this;
    }

    public UserDetails getUserDetails() {
        return userDetails;
    }

    public TheUser setUserDetails(UserDetails userDetails) {
        this.userDetails = userDetails;
        return this;
    }

    public Map<String, Borrow> getBorrowMap() {
        return borrowMap;
    }

    public TheUser setBorrowMap(Map<String, Borrow> borrowMap) {
        this.borrowMap = borrowMap;
        return this;
    }

    public Map<String, ReceivedBorrow> getReceivedBorrowMap() {
        return receivedBorrowMap;
    }

    public TheUser setReceivedBorrowMap(Map<String, ReceivedBorrow> receivedBorrowMap) {
        this.receivedBorrowMap = receivedBorrowMap;
        return this;
    }

    public Map<String, ReceivedBorrow> getHistory() {
        return history;
    }

    public TheUser setHistory(Map<String, ReceivedBorrow> history) {
        this.history = history;
        return this;
    }

    public Map<String, ReceivedBorrow> getMassages() {
        return massages;
    }

    public TheUser setMassages(Map<String, ReceivedBorrow> massages) {
        this.massages = massages;
        return this;
    }

}

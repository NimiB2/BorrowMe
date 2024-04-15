package com.project1.borrowme.models;

import java.util.HashMap;
import java.util.Map;

public class TheUser {
    private static volatile TheUser instance;
    private String uid;
    private UserDetails userDetails;
    private Map<String, Borrow> borrowMap;
    private Map<String, ReceivedBorrow> receivedBorrowMap;
    private Map<String, ReceivedBorrow> history;
    private Map<String, ReceivedBorrow> Messages;

    private TheUser() {
        userDetails = new UserDetails();
        receivedBorrowMap = new HashMap<>();
        history = new HashMap<>();
        Messages = new HashMap<>();
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

    public void addBorrow(String key, Borrow borrow) {
        borrowMap.put(key, borrow);
    }

    public void removeBorrow(String key) {
        borrowMap.remove(key);
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

    public Map<String, ReceivedBorrow> getMessages() {
        return Messages;
    }

    public TheUser setMessages(Map<String, ReceivedBorrow> Messages) {
        this.Messages = Messages;
        return this;
    }

    public <T> void addToMap(Map<String, T> map, String key, T value) {
        map.put(key, value);
    }

    // Generic method to remove an item from a map
    public <T> void removeFromMap(Map<String, T> map, String key) {
        if (map.containsKey(key)) {
            map.remove(key);
        }
    }
}

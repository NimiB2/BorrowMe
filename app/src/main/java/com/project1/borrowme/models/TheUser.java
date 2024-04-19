package com.project1.borrowme.models;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class TheUser {
    private static volatile TheUser instance;
    private String uid;
    private UserDetails userDetails;
    private Map<String, ReceivedBorrow> history;
    private Map<String, ReceivedBorrow> Messages;

    private TheUser() {
        userDetails = new UserDetails();
        history = new LinkedHashMap<>();
        Messages = new LinkedHashMap<>();
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
        if (map != null) {
            map.put(key, value);
        }
    }

    public <T> void removeFromMap(Map<String, T> map, String key) {
        if (map != null && map.containsKey(key)) {
            map.remove(key);
        }
    }

    public void resetUser() {

        uid = null;
        userDetails.resetUserDetails();
        history.clear();
        Messages.clear();

    }
}

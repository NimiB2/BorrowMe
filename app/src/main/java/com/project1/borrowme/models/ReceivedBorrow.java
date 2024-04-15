package com.project1.borrowme.models;

import com.project1.borrowme.adpters.UserAdapter;

import java.util.UUID;

public class ReceivedBorrow {
    private String id;
    private Borrow borrow;
    private UserAdapter receiveUser;
    private boolean isApprove;

    public ReceivedBorrow() {
        this.id = generateUniqueId();
        isApprove=false;
    }

    public ReceivedBorrow(Borrow borrow, UserAdapter receiveUser) {
        this.id = generateUniqueId();
        this.borrow = borrow;
        this.receiveUser = receiveUser;
        this.isApprove = false;
    }
    private static String generateUniqueId() {
        return UUID.randomUUID().toString();
    }

    public String getId() {
        return id;
    }

    public ReceivedBorrow setId(String id) {
        this.id = id;
        return this;
    }
    public Borrow getBorrow() {
        return borrow;
    }

    public ReceivedBorrow setBorrow(Borrow borrow) {
        this.borrow = borrow;
        return this;
    }

    public UserAdapter getReceiveUser() {
        return receiveUser;
    }

    public ReceivedBorrow setReceiveUser(UserAdapter receiveUser) {
        this.receiveUser = receiveUser;
        return this;
    }

    public boolean isApprove() {
        return isApprove;
    }

    public ReceivedBorrow setApprove(boolean approve) {
        isApprove = approve;
        return this;
    }
}

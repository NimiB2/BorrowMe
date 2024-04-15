package com.project1.borrowme.models;

import com.project1.borrowme.adpters.UserAdapter;

import java.util.UUID;

public class ReceivedBorrow {
    private String id;
    private Borrow borrow;
    private String receiveUserId;
    private boolean isApprove;

    public ReceivedBorrow() {
        this.id = generateUniqueId();
        isApprove=false;
    }

    public ReceivedBorrow(Borrow borrow, String receiveUserId) {
        this.id = generateUniqueId();
        this.borrow = borrow;
        this.receiveUserId = receiveUserId;
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

    public String getReceiveUserId() {
        return receiveUserId;
    }

    public ReceivedBorrow setReceiveUserId(String receiveUserId) {
        this.receiveUserId = receiveUserId;
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

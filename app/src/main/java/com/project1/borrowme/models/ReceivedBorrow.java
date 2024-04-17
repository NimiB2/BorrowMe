
package com.project1.borrowme.models;

import com.google.firebase.Timestamp;

import java.util.UUID;

public class ReceivedBorrow {
    private String id;
    private Timestamp createdAt;
    private Borrow borrow;
    private String receiveUserId;
    private boolean Approved;
    private boolean answer;

    public ReceivedBorrow() {
        Approved =false;
    }

    public ReceivedBorrow(Borrow borrow, String receiveUserId,Timestamp  createdAt) {
        this.borrow = borrow;
        this.id = borrow.getId();
        this.receiveUserId = receiveUserId;
        this.Approved = false;
        this.createdAt = createdAt;
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

    public boolean getApproved() {
        return Approved;
    }

    public ReceivedBorrow setApproved(boolean approved) {
        Approved = approved;
        return this;
    }

    public boolean getAnswer() {
        return answer;
    }

    public ReceivedBorrow setAnswer(boolean answer) {
        this.answer = answer;
        return this;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public ReceivedBorrow setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
        return this;
    }
}

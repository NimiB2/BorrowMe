
package com.project1.borrowme.models;

import java.util.UUID;

public class ReceivedBorrow {
    private String id;
    private Borrow borrow;
    private String receiveUserId;
    private boolean Approved;
    private boolean answer;
    private boolean me;

    public ReceivedBorrow() {
       // this.id = generateUniqueId();
        Approved =false;
    }

    public ReceivedBorrow(Borrow borrow, String receiveUserId) {

        this.borrow = borrow;
        this.id = borrow.getId();
        this.receiveUserId = receiveUserId;
        this.Approved = false;

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

    public boolean getMe() {
        return me;
    }

    public ReceivedBorrow setMe(boolean me) {
        this.me = me;
        return this;
    }

    public boolean getAnswer() {
        return answer;
    }

    public ReceivedBorrow setAnswer(boolean answer) {
        this.answer = answer;
        return this;
    }
}

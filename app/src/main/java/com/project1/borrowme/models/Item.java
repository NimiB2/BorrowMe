package com.project1.borrowme.models;

public class Item {
    private String name;
    private int numOfBorrow = 0;

    public Item() {
    }

    public String getName() {
        return name;
    }

    public Item setName(String name) {
        this.name = name;
        return this;
    }

    public int getNumOfBorrow() {
        return numOfBorrow;
    }

    public Item setNumOfBorrow() {
        this.numOfBorrow++;
        return this;
    }
}

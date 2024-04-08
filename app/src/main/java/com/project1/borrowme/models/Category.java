package com.project1.borrowme.models;

import java.util.LinkedHashSet;

import com.project1.borrowme.adpters.CategoryAdapter;

public class Category {
    private String name;
    private int image;

    private boolean isClicked = false;
    //private LinkedHashSet<Item> items = new LinkedHashSet<>();

    public Category() {
    }

    public Category(String name, int image, boolean isClicked) {
        this.name = name;
        this.image = image;
        this.isClicked = isClicked;
    }

    public String getName() {
        return name;
    }

    public Category setName(String name) {
        this.name = name;
        return this;
    }

    public Category setImage(int image) {
        this.image =image;
        return this;
    }

    public int getImage() {
        return image;
    }

    public boolean isClicked() {
        return isClicked;
    }
    public void setClicked(boolean clicked) {
        isClicked = clicked;
    }



//    public LinkedHashSet<Item> getItems() {
//        return items;
//    }
//    public void addItems(Item item){
//        if(item!=null){
//            items.add(item);
//        }
//    }
}

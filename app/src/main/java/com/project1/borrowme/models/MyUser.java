package com.project1.borrowme.models;

import android.content.Context;
import android.net.Uri;

import com.project1.borrowme.Utilities.MySignal;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;

public class MyUser {
    private static volatile MyUser instance;
    private Map<String, Category> categories = new HashMap<>();
    private String uid;
    private String uName;
    private String uEmail;
    private double lat;
    private double lan;

    private MyUser() {
    }

    public MyUser(Map<String, Category> categories, String uid, String uName, String uEmail, double lat, double lan) {
        this.categories = categories;
        this.uid = uid;
        this.uName = uName;
        this.uEmail = uEmail;
        this.lat = lat;
        this.lan = lan;
    }

    public static MyUser getInstance() {
        if (instance == null) {
            synchronized (MyUser.class) {
                if (instance == null) {
                    instance = new MyUser();
                }
            }
        }
        return instance;
    }

//     public static MyUser getInstance(){
//        return instance;
//    }


    public Map<String, Category> getCategories() {
        return categories;
    }

    public MyUser setCategories(Map<String, Category> categories) {
        this.categories = categories;
        return this;
    }

    public MyUser addCategory(Category category) {
        this.categories.put(category.getName(), category);
        return this;
    }

    public Category getCategoryByName(String categoryName) {
        return this.categories.get(categoryName);
    }

    public void removeCategory(String categoryName) {
        if(categories.size()>1){
            this.categories.remove(categoryName);
        }else{
            MySignal.getInstance().toast("You must have at least one category");
        }
    }


    public String getUid() {
        return uid;
    }

    public MyUser setUid(String uid) {
        this.uid = uid;
        return this;
    }

    public String getuName() {
        return uName;
    }

    public MyUser setuName(String uName) {
        this.uName = uName;
        return this;
    }

    public String getuEmail() {
        return uEmail;
    }

    public MyUser setuEmail(String uEmail) {
        this.uEmail = uEmail;
        return this;
    }

//    public Uri getPhoto() {
//        return photo;
//    }
//
//    public MyUser setPhoto(Uri photo) {
//        this.photo = photo;
//        return this;
//    }

    public double getLat() {
        return lat;
    }

    public MyUser setLat(double lat) {
        this.lat = lat;
        return this;
    }

    public double getLan() {
        return lan;
    }

    public MyUser setLan(double lan) {
        this.lan = lan;
        return this;
    }

    public void resetUser() {
        this.uid = null;
        this.uName = null;
        this.uEmail = null;
        this.lat = 0;
        this.lan = 0;
        this.categories.clear();
    }
}

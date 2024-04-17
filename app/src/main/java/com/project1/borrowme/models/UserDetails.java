package com.project1.borrowme.models;

import android.net.Uri;

import com.project1.borrowme.Utilities.MySignal;
import com.project1.borrowme.adpters.UserAdapter;

import java.util.HashMap;
import java.util.Map;

public class UserDetails {
    private Map<String, Category> categories = new HashMap<>();
    private String uName;
    private String uEmail;
    private double lat;
    private double lon;
    private Uri profileImageUri;

    public UserDetails() {
    }

    public Map<String, Category> getCategories() {
        return categories;
    }

    public UserDetails setCategories(Map<String, Category> categories) {
        this.categories = categories;
        return this;
    }

    public UserDetails addCategory(Category category) {
        this.categories.put(category.getName(), category);
        return this;
    }


    public void removeCategory(String categoryName) {
        if (categories.size() > 1) {
            this.categories.remove(categoryName);
        } else {
            MySignal.getInstance().toast("You must have at least one category");
        }
    }

    public String getuName() {
        return uName;
    }

    public UserDetails setuName(String uName) {
        this.uName = uName;
        return this;
    }

    public String getuEmail() {
        return uEmail;
    }

    public UserDetails setuEmail(String uEmail) {
        this.uEmail = uEmail;
        return this;
    }


    public double getLat() {
        return lat;
    }

    public UserDetails setLat(double lat) {
        this.lat = lat;
        return this;
    }

    public double getLon() {
        return lon;
    }

    public UserDetails setLon(double lon) {
        this.lon = lon;
        return this;
    }

    public Uri getProfileImageUri() {
        return profileImageUri;
    }

    public void setProfileImageUri(Uri profileImageUri) {
        this.profileImageUri = profileImageUri;
    }

    public UserAdapter getMyAdapter() {
        return new UserAdapter(this);
    }

    public void resetUserDetails() {

        categories = new HashMap<>();
        uName = null;
        uEmail = null;
        lat = 0.0;
        lon = 0.0;
        profileImageUri = null;

    }
}

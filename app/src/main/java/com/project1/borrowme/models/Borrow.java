package com.project1.borrowme.models;

import android.location.Location;

import com.project1.borrowme.adpters.UserAdapter;

import java.util.List;

public class Borrow {
    private String id;
    private UserAdapter fromUser;
    private boolean status;
    private boolean borrowComplete;
    private String itemName;
    private String description;
    private List<String> categories;
    private int distance;
    private int numOfSending;
    private int numOfAnswers;
    private Location location;

}

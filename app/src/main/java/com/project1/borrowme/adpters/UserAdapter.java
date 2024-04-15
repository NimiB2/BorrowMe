package com.project1.borrowme.adpters;

import android.net.Uri;

import com.project1.borrowme.models.UserDetails;

public class UserAdapter {
    private  UserDetails userDetails ;

    public UserAdapter() {
    }

    public UserAdapter(UserDetails userDetails) {
        this.userDetails = userDetails;
    }

    public String getuName(){
        return userDetails.getuName();
    }
    public String getuEmail(){

        return userDetails.getuEmail();
    }

}

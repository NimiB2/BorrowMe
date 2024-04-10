package com.project1.borrowme.adpters;

import com.project1.borrowme.models.MyUser;

public class UserAdapter {
    private final MyUser myUser=MyUser.getInstance();

    public UserAdapter(MyUser myUser) {
    }

    public String getUid(){
        return myUser.getUid();
    }
    public String getuName(){
        return myUser.getuName();
    }
    public String getuEmail(){

        return myUser.getuEmail();
    }


}

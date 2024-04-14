package com.project1.borrowme;

import android.app.Application;

import com.project1.borrowme.Utilities.MySignal;
import com.project1.borrowme.models.TheUser;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        MySignal.init(this);
        TheUser.getInstance();
    }
}

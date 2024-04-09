package com.project1.borrowme;

import android.app.Application;

import com.project1.borrowme.Utilities.MySignal;
import com.project1.borrowme.models.MyUser;

import java.util.HashMap;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        MySignal.init(this);
    }
}

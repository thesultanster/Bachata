package com.meetup.uhoo.util;

import android.app.Application;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Created by sultankhan on 9/18/16.
 */
public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();



    }

    public FirebaseUser getCurrentUser(){
        return FirebaseAuth.getInstance().getCurrentUser();
    }
}

package com.meetup.uhoo.service_layer.user_services;

import android.content.Context;
import android.content.SharedPreferences;

import com.meetup.uhoo.core.User;
import com.meetup.uhoo.core.UserDataFetchListener;

/**
 * Created by sultankhan on 1/12/17.
 */
public class UserDataService {

    private String uid;

    public UserDataService(String uid){
        this.uid = uid;
    }

    public void getFirebaseUserData(final UserDataFetchListener userDataFetchListener){

        FirebaseUserData firebaseUserData = new FirebaseUserData(uid);
        firebaseUserData.getUserData(new FirebaseDataFetchListener() {
            @Override
            public void onDataFetch(User user) {
                userDataFetchListener.onUserFetch(user);
            }
        });

    }





    /* Helpers
    /**************************************************************/



    /* Getters
    /**************************************************************/


    /* Setters
    /**************************************************************/
    public void setUid( String uid){
        this.uid = uid;
    }


}

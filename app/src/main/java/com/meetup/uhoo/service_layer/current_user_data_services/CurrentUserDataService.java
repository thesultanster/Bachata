package com.meetup.uhoo.service_layer.current_user_data_services;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.google.firebase.auth.FirebaseAuth;
import com.meetup.uhoo.Enum;
import com.meetup.uhoo.core.User;
import com.meetup.uhoo.core.UserDataFetchListener;
import com.meetup.uhoo.util.FindLocation;

/**
 * Created by sultankhan on 1/12/17.
 */
public class CurrentUserDataService {

    private Enum.AuthType authType;
    private String authToken;
    private String uid;
    private User currentUser;

    public void CurrentUserDataService(){

    }


    // Saves user data locally
    public void saveUserDataLocally(Context context){

        // Get user shared prefs and save account data locally
        SharedPreferences.Editor editor = context.getSharedPreferences("currentUser", context.MODE_PRIVATE).edit();
        editor.putString("firstName", currentUser.getFirstName());
        editor.putString("lastName", currentUser.getLastName());
        editor.putString("oneLiner", currentUser.getOneLiner());
        editor.putString("gender", currentUser.getGender());
        editor.putString("authType", authType.name());
        editor.apply();
    }

    public void getFirebaseUserData(final UserDataFetchListener userDataFetchListener){

        FirebaseUserData firebaseUserData = new FirebaseUserData(uid);
        firebaseUserData.getUserData(new FirebaseDataFetchListener() {
            @Override
            public void onDataFetch(User user) {
                currentUser = user;
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
    public void setAuthType(Enum.AuthType authType, String authToken){
        this.authType = authType;
        this.authToken = authToken;
    }

    public void setUid( String uid){
        this.uid = uid;
    }


}

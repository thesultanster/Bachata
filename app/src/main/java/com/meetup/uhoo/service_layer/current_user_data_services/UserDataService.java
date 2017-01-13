package com.meetup.uhoo.service_layer.current_user_data_services;

import android.content.Context;
import android.content.SharedPreferences;

import com.meetup.uhoo.Enum;
import com.meetup.uhoo.core.User;
import com.meetup.uhoo.core.UserDataFetchListener;

/**
 * Created by sultankhan on 1/12/17.
 */
public class UserDataService {

    private String uid;
    private User user;

    public void UserDataService(){

    }


    // Saves user data locally
    public void saveUserDataLocally(Context context){

        // Get user shared prefs and save account data locally
        SharedPreferences.Editor editor = context.getSharedPreferences("currentUser", context.MODE_PRIVATE).edit();
        editor.putString("firstName", user.getFirstName());
        editor.putString("lastName", user.getLastName());
        editor.putString("oneLiner", user.getOneLiner());
        editor.putString("gender", user.getGender());
        editor.apply();
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

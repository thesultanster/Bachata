package com.meetup.uhoo.service_layer.user_services;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.meetup.uhoo.Enum;
import com.meetup.uhoo.core.User;
import com.meetup.uhoo.core.UserDataFetchListener;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by sultankhan on 1/12/17.
 */
public class CurrentUserDataService {

    private Enum.AuthType authType;
    private String authToken;
    private String uid;
    private User currentUser;
    private Context context;

    public  CurrentUserDataService(Context context){
        this.context = context;
        getLocalUserData();
    }

    public User getLocalUserData(){

        // Get User Data if it Exists
        SharedPreferences sharedPrefs = context.getSharedPreferences("currentUser", 0);

        String oneLiner = sharedPrefs.getString("oneLiner", "");
        String firstName = sharedPrefs.getString("firstName","");
        String lastName = sharedPrefs.getString("lastName", "");
        String profileUrl = sharedPrefs.getString("photoUrl","");
        String gender = sharedPrefs.getString("gender","");
        String authType = sharedPrefs.getString("authType", "");
        String uid = sharedPrefs.getString("uid","");
        int checkinVisibilityState = sharedPrefs.getInt("checkinVisibilityState",0);

        double longitude = Double.parseDouble(sharedPrefs.getString("longitude",""));
        double latitude = Double.parseDouble(sharedPrefs.getString("latitude",""));

        this.uid = uid;
        this.authType = Enum.AuthType.valueOf(authType);
        currentUser = new User(firstName, lastName, oneLiner, profileUrl, gender, checkinVisibilityState);
        currentUser.longitude = longitude;
        currentUser.latitude = latitude;

        return currentUser;


    }

    // Saves user data locally
    public void saveUserDataLocally(Context context){

        // Get user shared prefs and save account data locally
        SharedPreferences.Editor editor = context.getSharedPreferences("currentUser", context.MODE_PRIVATE).edit();
        editor.putString("firstName", currentUser.getFirstName());
        editor.putString("lastName", currentUser.getLastName());
        editor.putString("oneLiner", currentUser.getOneLiner());
        editor.putString("gender", currentUser.getGender());
        editor.putString("latitude", Double.valueOf(currentUser.latitude).toString());
        editor.putString("longitude", Double.valueOf(currentUser.longitude).toString());
        editor.putString("authType", authType.name());
        editor.putString("photoUrl", currentUser.getPhotoUrl());
        editor.putInt("checkinVisibilityState", currentUser.getCheckinVisibilityState());
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


    public void saveUserToDatabase(){


        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/firstName/", currentUser.getFirstName() );
        childUpdates.put("/lastName/", currentUser.getLastName());
        childUpdates.put("/oneLiner/", currentUser.getOneLiner());
        childUpdates.put("/gender/", currentUser.getGender());
        childUpdates.put("/checkinVisibilityState/", currentUser.getCheckinVisibilityState());
        childUpdates.put("/photoUrl/", currentUser.getPhotoUrl());


        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("users").child(uid).updateChildren(childUpdates, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError == null) {
                    // After saving on database, save locally
                    saveUserDataLocally(context);
                }
            }
        });




    }




    /* Helpers
    /**************************************************************/



    /* Getters
    /**************************************************************/
    public User getCurrentUser(){
        return currentUser;
    }

    /* Setters
    /**************************************************************/
    public void setAuthType(Enum.AuthType authType, String authToken){
        this.authType = authType;
        this.authToken = authToken;
    }

    public void setCurrentUser(User currentUser){
        this.currentUser = currentUser;
    }
    public void setPhotoUrl( String photoUrl){
        currentUser.photoUrl = photoUrl;
    }
    public void setUid( String uid){
        this.uid = uid;
    }


}

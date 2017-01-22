package com.meetup.uhoo.core;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.meetup.uhoo.Enum;

import java.io.Serializable;
import java.lang.*;
import java.util.ArrayList;

/**
 * Created by sultankhan on 9/8/16.
 */
public class User implements Serializable{

    public transient UserDataFetchListener userDataFetchListener;
    public String firstName;
    public String lastName;
    public String photoUrl;
    public String gender;
    public String oneLiner;
    public String uid;
    public double latitude;
    public double longitude;
    public boolean isCheckedIn;
    public String checkedInto;
    public ArrayList<String> activityIconList;
    public int checkinVisibilityState;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
       init();
    }

    public User(String uid) {
        init();
        this.uid = uid;
        FetchUserData();
    }

    public User(String firstName, String lastName, String oneLiner) {
        init();
        this.firstName = firstName;
        this.lastName = lastName;
        this.oneLiner = oneLiner;
    }

    public User(String firstName, String lastName, String oneLiner, String photoUrl, String gender, int checkinVisibilityState) {
        init();
        this.firstName = firstName;
        this.lastName = lastName;
        this.oneLiner = oneLiner;
        this.photoUrl = photoUrl;
        this.gender = gender;
        this.checkinVisibilityState = checkinVisibilityState;
    }

    // Initialize user data with default values
    private void init(){
        this.firstName = "";
        this.lastName = "";
        this.oneLiner = "";
        this.isCheckedIn = false;
        this.checkedInto = "";
        this.gender = "";
        this.photoUrl = "";
        this.checkinVisibilityState = Enum.CheckinVisibilityState.AVAILABLE.getValue();
    }

    public String getFirstName(){
        return firstName;
    }
    public String getLastName(){
        return lastName;
    }
    public String getOneLiner(){
        return oneLiner;
    }
    public String getGender(){
        return gender;
    }
    public String getPhotoUrl(){ return photoUrl; }
    public int getCheckinVisibilityState(){ return checkinVisibilityState; }
    public ArrayList<String> getActivityIconList(){ return activityIconList; }


    public void FetchUserData(){
        DatabaseReference restaurantsRef = FirebaseDatabase.getInstance().getReference();
        restaurantsRef.child("users").child(uid).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        User user = dataSnapshot.getValue(User.class);

                        if(user != null) {
                            firstName = user.firstName;
                            lastName = user.lastName;
                            oneLiner = user.oneLiner;
                            isCheckedIn = user.isCheckedIn;
                            checkedInto = user.checkedInto;
                            gender = user.gender;

                            // Trigger interface
                            if (userDataFetchListener != null)
                                userDataFetchListener.onUserFetch(user);
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w("User", "Fetch Data Failed", databaseError.toException());
                    }
                });

    }

    public void setOnUserDataFetchListener(UserDataFetchListener listener) {
        userDataFetchListener = listener;
    }



}

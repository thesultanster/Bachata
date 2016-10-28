package com.meetup.uhoo;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.ValueEventListener;
import com.meetup.uhoo.restaurant.UserDataFetchListener;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * Created by sultankhan on 9/8/16.
 */
@IgnoreExtraProperties
public class User {

    private UserDataFetchListener userDataFetchListener;
    public String firstName;
    public String lastName;
    public String username;
    public String email;
    public String oneLiner;
    public String uid;
    public double latitude;
    public double longitude;
    public boolean isCheckedIn;
    public String checkedInto;
    public ArrayList<String> activityIconList;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
        this.firstName = "";
        this.lastName = "";
        this.oneLiner = "";
        this.isCheckedIn = false;
        this.checkedInto = "";

    }

    public User(String uid) {
        this.uid = uid;
        this.firstName = "First Last";
        this.lastName = "";
        this.oneLiner = "";
        this.isCheckedIn = false;
        this.checkedInto = "";

        FetchUserData();
    }

    public User(String firstName, String lastName, String oneLiner) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.oneLiner = oneLiner;
        this.isCheckedIn = false;
        this.checkedInto = "";
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

                            if (userDataFetchListener != null)
                                userDataFetchListener.onUserFetch(user); // event object :)
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w("User", "Fetch Data Failed", databaseError.toException());
                    }
                });

    }

    public void setOnEventListener(UserDataFetchListener listener) {
        userDataFetchListener = listener;
    }



}

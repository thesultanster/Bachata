package com.meetup.uhoo;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by sultankhan on 9/8/16.
 */
public class User implements Serializable{

    public transient UserDataFetchListener userDataFetchListener;
    public String firstName;
    public String lastName;
    public String username;
    public String gender;
    public String oneLiner;
    public String uid;
    public double latitude;
    public double longitude;
    public boolean isCheckedIn;
    public String checkedInto;
    public ArrayList<String> activityIconList;
    public Enum.CheckinVisibilityState checkinVisibilityState;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
        this.firstName = "";
        this.lastName = "";
        this.oneLiner = "";
        this.isCheckedIn = false;
        this.checkedInto = "";
        this.gender = "";
        this.checkinVisibilityState = Enum.CheckinVisibilityState.AVAILABLE;
    }

    public User(String uid) {
        this.uid = uid;
        this.firstName = "";
        this.lastName = "";
        this.oneLiner = "";
        this.isCheckedIn = false;
        this.checkedInto = "";
        this.gender = "";
        this.checkinVisibilityState = Enum.CheckinVisibilityState.AVAILABLE;
        FetchUserData();
    }

    public User(String firstName, String lastName, String oneLiner) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.oneLiner = oneLiner;
        this.isCheckedIn = false;
        this.checkedInto = "";
        this.checkinVisibilityState = Enum.CheckinVisibilityState.AVAILABLE;
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
    public ArrayList<String> getActivityIconList(){ return activityIconList; }
    // The Firebase data mapper will ignore this
    @Exclude
    public Enum.CheckinVisibilityState getCheckinVisibilityState() { return checkinVisibilityState; }

    public void setCheckinVisibilityState(int i){
        this.checkinVisibilityState =  Enum.CheckinVisibilityState.values()[i];
    }

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

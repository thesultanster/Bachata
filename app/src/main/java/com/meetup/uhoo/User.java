package com.meetup.uhoo;

import android.util.Log;

import com.firebase.geofire.GeoLocation;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.ValueEventListener;
import com.meetup.uhoo.restaurant.UserDataFetchListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sultankhan on 9/8/16.
 */
@IgnoreExtraProperties
public class User {

    private UserDataFetchListener userDataFetchListener;
    public String name_first;
    public String name_last;
    public String username;
    public String email;
    public String one_liner;
    public String uid;
    public double latitude;
    public double longitude;
    public boolean is_checked_in;
    public String checkedInto;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
        this.name_first = "";
        this.name_last = "";
        this.one_liner = "";
        this.is_checked_in = false;
        this.checkedInto = "";

    }

    public User(String uid) {
        this.uid = uid;
        this.name_first = "First Last";
        this.name_last = "";
        this.one_liner = "";
        this.is_checked_in = false;
        this.checkedInto = "";
        FetchUserData();
    }

    public User(String firstName, String lastName, String oneLiner) {
        this.name_first = firstName;
        this.name_last = lastName;
        this.one_liner = oneLiner;
        this.is_checked_in = false;
        this.checkedInto = "";
    }

    public String getName_first(){
        return name_first;
    }
    public String getName_last(){
        return name_last;
    }

    public String getOne_liner(){
        return one_liner;
    }

    public void FetchUserData(){
        DatabaseReference restaurantsRef = FirebaseDatabase.getInstance().getReference();
        restaurantsRef.child("users").child(uid).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        User user = dataSnapshot.getValue(User.class);

                        if(user != null) {
                            name_first = user.name_first;
                            name_last = user.name_last;
                            one_liner = user.one_liner;
                            is_checked_in = user.is_checked_in;
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

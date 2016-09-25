package com.meetup.uhoo;

import com.firebase.geofire.GeoLocation;
import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by sultankhan on 9/8/16.
 */
@IgnoreExtraProperties
public class User {

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
    }

    public User(String firstName, String lastName, String oneLiner) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.oneLiner = oneLiner;
        this.isCheckedIn = false;
        this.checkedInto = "";
    }

    public String getName(){
        return firstName + " " + lastName;
    }


}

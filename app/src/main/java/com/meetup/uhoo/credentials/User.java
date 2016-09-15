package com.meetup.uhoo.credentials;

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
    public GeoLocation location;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
        this.firstName = "";
        this.lastName = "";
        this.oneLiner = "";
    }

    public User(String uid, GeoLocation location) {
        this.uid = uid;
        this.location = location;
    }

    public User(String firstName, String lastName, String oneLiner) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.oneLiner = oneLiner;
    }


}

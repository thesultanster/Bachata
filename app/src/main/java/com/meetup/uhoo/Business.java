package com.meetup.uhoo;

import android.util.Log;

import com.google.android.gms.location.places.Place;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class Business implements Serializable{

    String name, address, placeId, phoneNumber;
    double latitude, longitude;
    int numUsersCheckedIn;
    List<User> users = Collections.emptyList();


    public Business(){

    }

    public Business(Place place)
    {
        this.name = place.getName().toString();
        this.address = place.getAddress().toString();
        this.placeId = place.getId();
        this.latitude = place.getLatLng().latitude;
        this.longitude = place.getLatLng().longitude;
        this.phoneNumber= place.getPhoneNumber().toString();
        place.freeze();

    }

    public String getPlaceId(){
        return placeId;
    }

    public String getPhoneNumber(){
        return phoneNumber;
    }

    public String getAddress(){
        return address;
    }

    public double getLatitude(){
        return latitude;
    }

    public double getLongitude(){
        return longitude;
    }

    public String getName(){
        return name;
    }

    public int getNumUsersCheckedIn(){
        return numUsersCheckedIn;
    }

    public void setNumUsersCheckedIn(int num){
        numUsersCheckedIn = num;
    }



}

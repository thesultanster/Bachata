package com.meetup.uhoo.restaurant;


import com.google.android.gms.location.places.Place;
import com.meetup.uhoo.credentials.User;

public class PeopleNearbyRecyclerInfo {

    String name, address;

    public PeopleNearbyRecyclerInfo(){
        this.name = "";
        this.address = "";
    }

    public PeopleNearbyRecyclerInfo(String key)
    {
        this.name = key;
    }



}

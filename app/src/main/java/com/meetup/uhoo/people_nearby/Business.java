package com.meetup.uhoo.people_nearby;

import com.google.android.gms.location.places.Place;

public class Business {

    String name, address, placeId;

    public Business(){
        this.name = "";
        this.address = "";
        this.placeId = "";
    }

    public Business(Place place)
    {
        this.name = place.getName().toString();
        this.address = place.getAddress().toString();
        this.placeId = place.getId();
        place.freeze();
    }

    public Business(String key)
    {
        this.name = key;
    }



}

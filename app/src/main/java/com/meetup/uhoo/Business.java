package com.meetup.uhoo;

import com.google.android.gms.location.places.Place;

import java.io.Serializable;

public class Business implements Serializable{

    String name, address, placeId, phoneNumber;
    double latitude, longitude;


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
        this.latitude = place.getLatLng().latitude;
        this.longitude = place.getLatLng().longitude;
        this.phoneNumber= place.getPhoneNumber().toString();
        place.freeze();
    }

    public Business(String key)
    {
        this.name = key;
    }

    public String getId(){
        return placeId;
    }

    public String getName(){
        return name;
    }


}

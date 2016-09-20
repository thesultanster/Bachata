package com.meetup.uhoo.people_nearby;


import com.google.android.gms.location.places.Place;
import com.google.android.gms.maps.model.LatLng;

public class RestaurantsNearbyRecyclerInfo {

    String name, address;

    public  RestaurantsNearbyRecyclerInfo(){
        this.name = "";
        this.address = "";
    }

    public RestaurantsNearbyRecyclerInfo(Place place)
    {
        this.name = place.getName().toString();
        this.address = place.getAddress().toString();

        place.freeze();
    }

    public RestaurantsNearbyRecyclerInfo(String key)
    {
        this.name = key;
    }



}

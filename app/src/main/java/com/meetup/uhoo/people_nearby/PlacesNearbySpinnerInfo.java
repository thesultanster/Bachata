package com.meetup.uhoo.people_nearby;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.maps.model.LatLng;

/**
 * Created by sultankhan on 9/17/16.
 */
public class PlacesNearbySpinnerInfo {
    private String name;
    private double latitude;
    private double longitude;
    private Place place;

    public PlacesNearbySpinnerInfo(Place place){

        this.place = place.freeze();
        this.name = place.getName().toString();
    }

    public PlacesNearbySpinnerInfo(String name, double latitude, double longitude){
        this.name = name;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getName(){
        return this.name;
    }

    public double getLatitude() {
        return this.place.getLatLng().latitude;
    }

    public  double getLongitude(){
        return this.place.getLatLng().longitude;
    }

    public Place getPlace(){
        return this.place;
    }

}

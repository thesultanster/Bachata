package com.meetup.uhoo.core;

import com.google.android.gms.location.places.Place;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

public class Business implements Serializable{

    private String name, address, placeId, phoneNumber;
    private double latitude, longitude;
    private int numUsersCheckedIn;
    private int numHappenings;
    public List<User> usersCheckedIn = Collections.emptyList();

    public transient UserCheckinListener userCheckinListener;


    public Business(){

    }

    public Business(Place place)
    {
        this.name = place.getName().toString();
        this.address = place.getAddress().toString();
        this.placeId = place.getId();
        this.latitude = place.getLatLng().latitude;
        this.longitude = place.getLatLng().longitude;
        this.phoneNumber = place.getPhoneNumber().toString();

        numUsersCheckedIn = 0;
        numHappenings = 0;

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

    public int getNumHappenings(){
        return numHappenings;
    }

    public void setNumUsersCheckedIn(int num){
        numUsersCheckedIn = num;
    }

    public void setUsersCheckedIn( List<User> usersCheckedIn){
        this.usersCheckedIn = usersCheckedIn;


    }

    public void setOnUserCheckinListener( UserCheckinListener userCheckinListener){
        this.userCheckinListener = userCheckinListener;
    }


     public void FetchCheckedInUserData(){

        int position = 0;
        for( User user : usersCheckedIn){
            final int finalPosition = position;
            user.setOnUserDataFetchListener(new UserDataFetchListener() {
                @Override
                public void onUserFetch(User user) {
                    // Update List with new user
                    usersCheckedIn.set(finalPosition, user);
                    // Trigger the interface
                    userCheckinListener.onFetchUsersCheckedIn(usersCheckedIn);
                }
            });
            user.FetchUserData();

            position++;
        }
    }




}

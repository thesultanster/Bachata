package com.meetup.uhoo.restaurant;

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

    public String getName(){
        return name;
    }



}

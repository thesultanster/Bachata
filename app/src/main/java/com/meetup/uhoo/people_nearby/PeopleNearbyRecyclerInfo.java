package com.meetup.uhoo.people_nearby;


public class PeopleNearbyRecyclerInfo {

    String name;
    String miniBio;

    public PeopleNearbyRecyclerInfo(String name, String miniBio)
    {
        super();
        this.name = name;
        this.miniBio = miniBio;

    }
    public String getName()
    {
        return name;
    }



    public String getMiniBio(){
        return miniBio;

    }
}

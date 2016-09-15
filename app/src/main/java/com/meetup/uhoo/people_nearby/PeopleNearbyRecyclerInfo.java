package com.meetup.uhoo.people_nearby;


import com.meetup.uhoo.credentials.User;

public class PeopleNearbyRecyclerInfo {

    String name;
    String miniBio;
    User user;

    public PeopleNearbyRecyclerInfo(String name, String miniBio)
    {
        super();
        this.name = name;
        this.miniBio = miniBio;

    }

    public PeopleNearbyRecyclerInfo(User user)
    {
        super();
        this.name = user.uid;
        this.miniBio = user.uid;

    }
    public String getName()
    {
        return name;
    }



    public String getMiniBio(){
        return miniBio;

    }
}

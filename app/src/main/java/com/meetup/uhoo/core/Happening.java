package com.meetup.uhoo.core;

import com.meetup.uhoo.Enum;

import java.io.Serializable;
import java.lang.*;

/**
 * Created by sultankhan on 11/26/16.
 */
public class Happening implements Serializable{

    private String title;
    private String description;
    private Enum.HappeningType type;

    public Happening(){

    }

    public Happening(String title, String description, Enum.HappeningType type){
        this.title = title;
        this.description = description;
        this.type = type;
    }


    public String getTitle(){
        return title;
    }

    public String getDescription(){
        return description;
    }

    public Enum.HappeningType getType(){
        return type;
    }

}

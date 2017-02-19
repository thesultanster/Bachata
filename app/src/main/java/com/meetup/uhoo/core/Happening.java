package com.meetup.uhoo.core;

import com.meetup.uhoo.Enum;

import java.io.Serializable;
import java.lang.*;

/**
 * Created by sultankhan on 11/26/16.
 */
public class Happening implements Serializable{

    private String title;
    private String body;
    private String time;
    private String type;
    private String icon;

    public Happening(){

    }

    public Happening(String title, String body, String type, String icon, String time){
        this.title = title;
        this.body = body;
        this.time = time;
        this.type = type;
        this.icon = icon;
    }


    public String getTitle(){
        return title;
    }

    public String getBody(){
        return body;
    }

    public String getTime() { return time; }

    public String getType() { return type; }

    public String getIcon() { return icon; }


}

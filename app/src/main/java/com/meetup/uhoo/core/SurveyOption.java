package com.meetup.uhoo.core;

/**
 * Created by sultankhan on 12/18/16.
 */
public class SurveyOption {
    String title;
    Boolean value;

    public SurveyOption(){

    }

    public SurveyOption(String title, Boolean value){
        this.title = title;
        this.value = value;
    }

    public String getTitle(){
        return this.title;
    }

    public void setValue(boolean value){
        this.value = value;
    }

}

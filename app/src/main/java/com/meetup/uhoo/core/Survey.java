package com.meetup.uhoo.core;

import com.meetup.uhoo.service_layer.SurveyDataFetchListener;
import com.meetup.uhoo.service_layer.SurveyService;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by sultankhan on 12/18/16.
 */
public class Survey implements Serializable {


    private String surveyId, businessId, title, body, type;
    private List<SurveyOption> options;



    public Survey() {
        this.options = new ArrayList<>();
        options.add(new SurveyOption("Yes", Boolean.FALSE ));
        options.add(new SurveyOption("No", Boolean.FALSE ));
        this.type = "SINGLE_SELECT";
    }


    public String getSurveyId(){
        return surveyId;
    }

    public String getTitle(){
        return title;
    }

    public String getBody(){
        return body;
    }

    public String getBusinessId(){
        return businessId;
    }

    public String getType() { return type; }

    public List<SurveyOption> getOptions(){
        return options;
    }





    public void setOptions(List<SurveyOption> options){
        this.options = options;
    }

    public void setSurveyId(String surveyId){
        this.surveyId = surveyId;
    }

    public void setBusinessId(String businessId){
        this.businessId = businessId;
    }

    public void setTitle(String title){
        this.title = title;
    }

    public void setBody(String body){
        this.body = body;
    }

    public void setType(String type) { this.type = type; }

}

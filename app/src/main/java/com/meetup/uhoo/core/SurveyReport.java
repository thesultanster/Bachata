package com.meetup.uhoo.core;

import java.util.Date;
import java.util.List;

/**
 * Created by sultankhan on 1/17/17.
 */
public class SurveyReport{

    private String surveyId;
    private String businessId;
    private String userId;
    private List<SurveyOption> optionsList;
    private Long dateInLong;


    public  SurveyReport(){

    }

    public  SurveyReport(  String surveyId, String businessId, String userId, List<SurveyOption> optionsList ){
        this.surveyId = surveyId;
        this.businessId = businessId;
        this.userId = userId;
        this.optionsList = optionsList;
        this.dateInLong = new Date().getTime();
    }

    public String getSurveyId() {
        return surveyId;
    }

    public String getBusinessId() {
        return businessId;
    }

    public String getUserId() {
        return userId;
    }

    public List<SurveyOption> getOptionsList() {
        return optionsList;
    }

    public Long getDateInLong() {
        return dateInLong;
    }
}

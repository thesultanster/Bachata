package com.meetup.uhoo.service;


import com.meetup.uhoo.core.Survey;

/**
 * Created by sultankhan on 9/25/16.
 */
public interface SurveyDataFetchListener {
    void onSurveyFetchCompleted(Survey object);
}

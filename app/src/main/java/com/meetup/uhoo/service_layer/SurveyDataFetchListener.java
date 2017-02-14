package com.meetup.uhoo.service_layer;


import com.meetup.uhoo.core.Survey;

/**
 * Created by sultankhan on 9/25/16.
 */
public interface SurveyDataFetchListener {
    void onSurveyFetched(Survey object);
    void onNoSurveys();
}

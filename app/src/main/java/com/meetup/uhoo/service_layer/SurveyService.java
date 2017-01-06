package com.meetup.uhoo.service_layer;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.meetup.uhoo.core.Survey;

/**
 * Created by sultankhan on 12/18/16.
 */
public class SurveyService {


    private SurveyDataFetchListener surveyDataFetchListener;
    private DatabaseReference mDatabase;
    private Survey survey;


    public SurveyService() {
        this.mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    public SurveyService(Survey survey) {
        this.survey = survey;
        this.mDatabase = FirebaseDatabase.getInstance().getReference();
    }


    public Survey upload() {

        // If this is a brand new survey with no surveyId, then create an Id
        if (survey.getSurveyId().equals("") || survey.getSurveyId() == null) {
            survey.setSurveyId(createNewSurveyInDatabase());
        }

        // TODO: Implement callback and dialog progress for better UX
        // Upload survey to database
        mDatabase.child("surveys").child(survey.getSurveyId()).setValue(survey);

        // Add survey to business_survey relation
        mDatabase.child("business_survey_relation").child(survey.getBusinessId()).child(survey.getSurveyId()).setValue(0);


        return survey;
    }

    public void fetch(final SurveyDataFetchListener surveyDataFetchListener) {

        // If brand new survey, then just return. There's nothing to query
        if (survey.getSurveyId() == null || survey.getSurveyId().equals("")) {

            // Trigger interface to send existing survey
            if (surveyDataFetchListener != null)
                surveyDataFetchListener.onSurveyFetchCompleted(survey);

        } // If survey exists, then fetch data from database
        else {
            fetchSurveyFromDatabase(survey.getSurveyId());
        }


    }


    private String createNewSurveyInDatabase() {
        String key = mDatabase.child("survey").push().getKey();
        return key;
    }

    private void fetchSurveyFromDatabase(String surveyId) {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("surveys").child(surveyId).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        Survey survey = dataSnapshot.getValue(Survey.class);

                        // Trigger interface and send queried survey
                        if (surveyDataFetchListener != null)
                            surveyDataFetchListener.onSurveyFetchCompleted(survey);

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w("User", "Fetch Data Failed", databaseError.toException());
                    }
                });
    }

    public void fetchBusinessSurveys( String businessId, final SurveyDataFetchListener surveyDataFetchListener){
        // Query Dashboard items by only checking surveys right now
        mDatabase.child("business_survey_relation").child(businessId).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        // Go through evey survey, change it to Dashboarditem format and trigger
                        for (DataSnapshot survey : dataSnapshot.getChildren()) {

                            Survey temp = dataSnapshot.getValue(Survey.class);
                            temp.setSurveyId(survey.getKey());


                            fetchSurveysInAShittyManner(temp, surveyDataFetchListener);
                        }


                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.e("Dashboard Service", "getDashboardItems:onCancelled", databaseError.toException());
                    }
                });
    }

    private void fetchSurveysInAShittyManner(Survey survey, final SurveyDataFetchListener surveyDataFetchListener){
        DatabaseReference mDashboardRef = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("surveys").child(survey.getSurveyId()).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {


                        Survey temp = dataSnapshot.getValue(Survey.class);

                        // Trigger interface
                        if (surveyDataFetchListener != null)
                            surveyDataFetchListener.onSurveyFetchCompleted(temp);




                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.e("Dashboard Service", "getDashboardItems:onCancelled", databaseError.toException());
                    }
                });

    }

    public void answerSurvey(boolean answer){

        mDatabase.child("survey_report")
                .child(survey.getSurveyId())
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .setValue(answer);

    }






}

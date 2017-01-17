package com.meetup.uhoo.service_layer;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.meetup.uhoo.core.Survey;
import com.meetup.uhoo.core.SurveyOption;
import com.meetup.uhoo.core.SurveyReport;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by sultankhan on 12/18/16.
 */
public class SurveyService {

    private SurveyDataFetchListener surveyDataFetchListener;
    private DatabaseReference mDatabase;


    public SurveyService() {
        this.mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    private String createNewSurveyInDatabase() {
        String key = mDatabase.child("survey").push().getKey();
        return key;
    }

    private void fetchSurveyFromDatabase(String surveyId, final SurveyDataFetchListener surveyDataFetchListener) {
        Log.i("fetchSurveyFromDatabase", "surveyId: " + surveyId);

        DatabaseReference surveyRef = FirebaseDatabase.getInstance().getReference();
        surveyRef.child("surveys").child(surveyId).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Survey survey = dataSnapshot.getValue(Survey.class);


                        /*
                        Map<Integer, SurveyOption> td = new HashMap<Integer, SurveyOption>();
                        for (Object object: survey.getOptions()) {

                            td.put(jobSnapshot.getKey(), (SurveyOption) object);
                        }

                        ArrayList<Job_Class> values = new ArrayList<>(td.values());

                        */


                        Log.i("fetchSurveyFromDatabase", "onDataChange:surveyTitle: " + survey.getTitle());

                        // Trigger interface and send queried survey
                        if (surveyDataFetchListener != null)
                            surveyDataFetchListener.onSurveyFetched(survey);

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w("fetchSurveyFromDatabase", "Fetch Data Failed", databaseError.toException());
                    }
                });
    }

    public void fetchBusinessSurveys(String businessId, final SurveyDataFetchListener surveyDataFetchListener) {
        Log.i("fetchBusinessSurveys", "businessID: " + businessId);

        // Query Dashboard items by only checking surveys right now
        mDatabase.child("business_survey_relation").child(businessId).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Go through evey survey, change it to Dashboarditem format and trigger
                        for (DataSnapshot survey : dataSnapshot.getChildren()) {
                            Log.i("fetchBusinessSurveys", "onDataChange: " + survey.getKey());
                            fetchSurveyFromDatabase(survey.getKey(), surveyDataFetchListener);
                        }


                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.e("fetchBusinessSurveys", "fetchBusinessSurveys:onCancelled", databaseError.toException());
                    }
                });
    }


    public void generateSurveyReport(Survey survey) {

        SurveyReport surveyReport = new SurveyReport(survey.getSurveyId(), survey.getBusinessId(), FirebaseAuth.getInstance().getCurrentUser().getUid().toString(), survey.getOptions());

        mDatabase.child("survey_report")
                .child(survey.getSurveyId())
                .push()
                .setValue(surveyReport).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        // If Error
                        if (!task.isSuccessful()) {
                            Log.e("Survey Report", "Error" + task.getException().toString());
                        }

                        Log.i("Survey Report", "onComplete:Success");
                    }
                });


    }


}

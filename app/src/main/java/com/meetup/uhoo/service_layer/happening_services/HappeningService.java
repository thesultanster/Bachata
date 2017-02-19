package com.meetup.uhoo.service_layer.happening_services;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.meetup.uhoo.core.Happening;
import com.meetup.uhoo.core.Survey;
import com.meetup.uhoo.service_layer.SurveyDataFetchListener;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by sultankhan on 2/18/17.
 */
public class HappeningService {

    DatabaseReference mDatabase;

    public HappeningService() {
        this.mDatabase = FirebaseDatabase.getInstance().getReference();
    }


    private void fetchHappeningFromDatabase(String happeningId, final HappeningDataFetchListener happeningDataFetchListener) {
        Log.i("fetchHappeningFromDb", "happeningId: " + happeningId);

        DatabaseReference surveyRef = FirebaseDatabase.getInstance().getReference();
        surveyRef.child("happenings").child(happeningId).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Happening happening = dataSnapshot.getValue(Happening.class);


                        Log.i("fetchHappeningFromDb", "onDataChange:happeningTitle: " + happening.getTitle());

                        // Trigger interface and send queried survey
                        if (happeningDataFetchListener != null)
                            happeningDataFetchListener.onHappeningFetched(happening);

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w("fetchSurveyFromDatabase", "Fetch Data Failed", databaseError.toException());
                    }
                });
    }

    public void fetchHappenings(String businessId, final HappeningDataFetchListener happeningDataFetchListener) {
        Log.i("fetchHappenings", "businessID: " + businessId);

        // Query Dashboard items by only checking surveys right now
        mDatabase.child("business_happening_relation").child(businessId).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Log.i("onDataChange", "happening exist: " + dataSnapshot.exists());


                        ArrayList<String> happenings = new ArrayList<String>();
                        // Go through evey survey, change it to Dashboarditem format and trigger
                        for (DataSnapshot survey : dataSnapshot.getChildren()) {
                            happenings.add((String) survey.getValue());
                            fetchHappeningFromDatabase((String) survey.getValue(), happeningDataFetchListener);
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.e("fetchHappenings", "fetchHappenings:onCancelled", databaseError.toException());
                    }
                });
    }
}

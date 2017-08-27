package com.meetup.uhoo.service_layer.analytics_engine;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.meetup.uhoo.core.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by sultankhan on 5/6/17.
 */
public class AnalyticsService {

    private static AnalyticsService ourInstance = new AnalyticsService();

    public static AnalyticsService getInstance() {
        return ourInstance;
    }

    private AnalyticsService() {

    }


    public void logHappeningClick(String happeningId, String userId){

        // TODO: Replace this in future with server side code that tracks num users checked in
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("analytics").child("happenings").child(happeningId).child("clicks").child(userId).push().setValue(System.currentTimeMillis(), new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError == null) {
                    Log.i("AnalyticsService:","logHappeningClick: success");
                }
            }
        });

    }



}

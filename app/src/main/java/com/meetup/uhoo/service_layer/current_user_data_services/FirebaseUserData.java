package com.meetup.uhoo.service_layer.current_user_data_services;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.meetup.uhoo.core.User;

/**
 * Created by sultankhan on 1/12/17.
 */
public class FirebaseUserData {

    private String uid;
    private FirebaseDataFetchListener firebaseDataFetchListener;

    public FirebaseUserData(String uid) {
        this.uid = uid;
    }


    public void FirebaseUserData() {}



    public void getUserData(final FirebaseDataFetchListener firebaseDataFetchListener) {
        this.firebaseDataFetchListener = firebaseDataFetchListener;

        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference();
        userRef.child("users").child(uid).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        User user = dataSnapshot.getValue(User.class);

                        // Trigger interface
                        if (firebaseDataFetchListener != null)
                            firebaseDataFetchListener.onDataFetch(user);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w("User", "Fetch Data Failed", databaseError.toException());
                    }
                });

    }

     /* Helpers
    /**************************************************************/


     /* Getters
    /**************************************************************/


     /* Setters
    /**************************************************************/


}

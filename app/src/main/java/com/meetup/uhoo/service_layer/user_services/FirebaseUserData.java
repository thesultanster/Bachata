package com.meetup.uhoo.service_layer.user_services;

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

    public FirebaseUserData(String uid) {
        this.uid = uid;
    }



    public void getUserData(final FirebaseDataFetchListener firebaseDataFetchListener) {
        Log.i("FirebaseUserData", "getUserData:uid: " + uid);

        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference();
        userRef.child("users").child(uid).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Log.i("FirebaseUserData", "onDataChange:exists: " + dataSnapshot.exists());
                        for (DataSnapshot child : dataSnapshot.getChildren()) {
                            if (child.getKey().equals("firstName")) {
                                Log.i("FirebaseUserData", "child: " + child.getValue().toString());
                            }
                            if (child.getKey().equals("lastName")) {
                                Log.i("FirebaseUserData", "child: " + child.getValue().toString());
                            }
                            if (child.getKey().equals("isCheckedIn")) {
                                Log.i("FirebaseUserData", "child: " + child.getValue().toString());
                            }

                        }

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

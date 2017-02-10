package com.meetup.uhoo.service_layer.business_services;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.meetup.uhoo.core.Business;
import com.meetup.uhoo.core.User;
import com.meetup.uhoo.core.UserCheckinListener;
import com.meetup.uhoo.core.UserDataFetchListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by sultankhan on 1/25/17.
 */
public class BusinessService {

    Business business;

    public BusinessService(){

    }

    public BusinessService(Business business){
        this.business = business;
    }

    // Public
    public void fetchBusiness(String businessId , final BusinessNearbyListener businessNearbyListener){
        DatabaseReference restaurantsRef = FirebaseDatabase.getInstance().getReference();
        restaurantsRef.child("restaurants").child(businessId).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Log.i("BusinessService", "onDataChange:exists " + dataSnapshot.exists());

                        // If datasnapshot returns null, then business doesnt exist on database
                        if( !dataSnapshot.exists()){
                            businessNearbyListener.onBusinessDoesntExist();
                        }


                        // Bind Business object
                        final Business restaurant = dataSnapshot.getValue(Business.class);

                        if (restaurant != null) {

                            // Query the users checked into the restaurant in order to display number of users
                            DatabaseReference restaurantsRef = FirebaseDatabase.getInstance().getReference();
                            restaurantsRef.child("checkin").child(restaurant.getPlaceId()).addListenerForSingleValueEvent(
                                    new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {

                                            // Save users in list
                                            final List<User> users = new ArrayList<User>();
                                            for (DataSnapshot user : dataSnapshot.getChildren()) {

                                                // Create a user object and sets its ID
                                                // This is a placeholder gridview item for the checkedin users gridview
                                                // Later on we will use the Ids to query user gender and update gridview
                                                User tempUser = new User();
                                                tempUser.uid = user.getKey();

                                                users.add(tempUser);

                                            }

                                            restaurant.setUsersCheckedIn(users);
                                            restaurant.setNumUsersCheckedIn(users.size());
                                            // TODO: Replace this in future with server side code that tracks num users checked in
                                            Map<String, Object> childUpdates = new HashMap<>();
                                            childUpdates.put("/numUsersCheckedIn/", users.size());
                                            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
                                            mDatabase.child("restaurants").child(restaurant.getPlaceId()).updateChildren(childUpdates, new DatabaseReference.CompletionListener() {
                                                @Override
                                                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                                    if (databaseError == null) {

                                                    }
                                                }
                                            });

                                            businessNearbyListener.onBusinessFetched( restaurant );
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {
                                            Log.e("BusinessService", "UserCheckinQuery:onCancelled", databaseError.toException());
                                        }
                                    });
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w("BusinessService", "Fetch Data Failed", databaseError.toException());
                        businessNearbyListener.onFetchComplete();
                    }
                });
    }

    public void fetchCheckedInUserData( final UserCheckinListener userCheckinListener){


        int position = 0;
        for( User user : business.usersCheckedIn){
            //Log.i("BusinessService", "fetchCheckedInUserData:user " + user.getUid());

            if(user.getUid() == null){
                business.usersCheckedIn.set(position, new User());
                // Trigger the interface
                userCheckinListener.onFetchUsersCheckedIn(business.usersCheckedIn);
                position++;
                return;
            }

            final int finalPosition = position;
            user.setOnUserDataFetchListener(new UserDataFetchListener() {
                @Override
                public void onUserFetch(User user) {
                    // Update List with new user
                    business.usersCheckedIn.set(finalPosition, user);
                    // Trigger the interface
                    userCheckinListener.onFetchUsersCheckedIn(business.usersCheckedIn);
                }
            });
            user.FetchUserData();

            position++;
        }
    }

    // Helpers

    // Getters

    // Setters
}

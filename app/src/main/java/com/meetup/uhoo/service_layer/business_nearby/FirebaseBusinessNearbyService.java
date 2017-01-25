package com.meetup.uhoo.service_layer.business_nearby;

import android.util.Log;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.meetup.uhoo.core.Business;
import com.meetup.uhoo.core.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by sultankhan on 1/25/17.
 */
public class FirebaseBusinessNearbyService {

    private static FirebaseBusinessNearbyService instance;

    private GeoQuery geoQuery;
    private GeoFire geoFire;
    private DatabaseReference databaseRef;

    public FirebaseBusinessNearbyService(){
        databaseRef =  FirebaseDatabase.getInstance().getReference("restaurant_locations");
        geoFire = new GeoFire(databaseRef);
    }


    // Public
    public void startNearbyService( Double longitude, Double latitude, final BusinessNearbyListener businessNearbyListener){

        // Given Manual Location, query for changes in all object in a 0.6ki radius
        geoQuery = geoFire.queryAtLocation(new GeoLocation(latitude, longitude), 0.6);
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                System.out.println(String.format("Key %s entered the search area at [%f,%f]", key, location.latitude, location.longitude));


                // After Querying the Key then query restaurant information
                DatabaseReference restaurantsRef = FirebaseDatabase.getInstance().getReference();
                restaurantsRef.child("restaurants").child(key).addListenerForSingleValueEvent(
                        new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

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
                                                    Log.e("FirebaseBusinessNearby", "UserCheckinQuery:onCancelled", databaseError.toException());
                                                }
                                            });
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Log.e("FirebaseBusinessNearby", "BusinessNearbyQuery:onCancelled", databaseError.toException());
                                businessNearbyListener.onFetchComplete();
                            }
                        });


            }

            @Override
            public void onKeyExited(String key) {
                Log.i("FirebaseBusinessNearby", "GeoFireQuery:onKeyExited: " + String.format("Key %s is no longer in the search area", key));
            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {
                Log.i("FirebaseBusinessNearby", "GeoFireQuery:onKeyMoved: " + String.format("Key %s moved within the search area to [%f,%f]", key, location.latitude, location.longitude));
            }

            @Override
            public void onGeoQueryReady() {
                Log.i("FirebaseBusinessNearby", "GeoFireQuery:onGeoQueryReady: " + "All initial data has been loaded and events have been fired!");
                businessNearbyListener.onFetchComplete();
            }

            @Override
            public void onGeoQueryError(DatabaseError error) {
                Log.e("FirebaseBusinessNearby", "GeoFireQuery:onCancelled", error.toException());
                businessNearbyListener.onFetchComplete();
            }
        });

    }


    public void stopNearbyListerner(){
        if (geoQuery != null)
            geoQuery.removeAllListeners();
    }

    // Return Firebase Instance
    public static FirebaseBusinessNearbyService getInstance() {
        if(instance == null)
            instance = new FirebaseBusinessNearbyService();

        return instance;
    }

    // Helpers


    // Getters


}

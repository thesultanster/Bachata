package com.meetup.uhoo.service_layer.business_services;

import android.util.Log;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by sultankhan on 1/25/17.
 */
public class FirebaseBusinessNearbyService {

    private static FirebaseBusinessNearbyService instance;

    private GeoQuery geoQuery;
    private GeoFire geoFire;
    private DatabaseReference databaseRef;

    public FirebaseBusinessNearbyService() {
        databaseRef = FirebaseDatabase.getInstance().getReference("restaurant_locations");
        geoFire = new GeoFire(databaseRef);
    }


    // Public
    public void startNearbyService(Double longitude, Double latitude, final BusinessNearbyListener businessNearbyListener) {

        // Given Manual Location, query for changes in all object in a 0.6ki radius
        geoQuery = geoFire.queryAtLocation(new GeoLocation(latitude, longitude), 0.6);
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                System.out.println(String.format("Key %s entered the search area at [%f,%f]", key, location.latitude, location.longitude));


                BusinessService businessService = new BusinessService();
                businessService.fetchBusiness(key, businessNearbyListener);



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


    public void stopNearbyListerner() {
        if (geoQuery != null)
            geoQuery.removeAllListeners();
    }

    // Return Firebase Instance
    public static FirebaseBusinessNearbyService getInstance() {
        if (instance == null)
            instance = new FirebaseBusinessNearbyService();

        return instance;
    }

    // Helpers


    // Getters


}

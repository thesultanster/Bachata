package com.meetup.uhoo.service_layer.business_services;

import android.content.Context;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.common.api.GoogleApiClient;
import com.meetup.uhoo.core.Business;

import java.util.ArrayList;

/**
 * Created by sultankhan on 1/25/17.
 */
public class BusinessNearbyService {

    private ArrayList<Business> loadedBusinesses;
    private FirebaseBusinessNearbyService firebaseBusinessNearbyService;
    private GooglePlacesNearbyService googlePlacesNearbyService;
    private static BusinessNearbyService instance;

    public BusinessNearbyService(){
        this.loadedBusinesses = new ArrayList<Business>(){
            @Override
            public boolean add(Business business) {

                    for ( Business loadedBusiness : loadedBusinesses){
                        if(loadedBusiness.getPlaceId().equals(business.getPlaceId())){
                            return false;
                        }
                    }

                    return super.add(business);

                }

            };
    }

    // Public
    public void getNearbyBusinesses(Double longitude, Double latitude, final BusinessNearbyListener businessNearbyListener, Context context, FragmentActivity fragmentActivity, GoogleApiClient.OnConnectionFailedListener onConnectionFailedListener ){

       BusinessNearbyListener businessNearbyListener1 = new BusinessNearbyListener() {
           @Override
           public void onBusinessFetched(Business object) {
               loadedBusinesses.add(object);
               businessNearbyListener.onBusinessFetched(object);
           }

           @Override
           public void onFetchComplete(ArrayList<Business> loadedBusinesses) {
               businessNearbyListener.onFetchComplete();
           }

           @Override
           public void onFetchComplete() {
               //businessNearbyListener.onFetchComplete(loadedBusinesses);
               businessNearbyListener.onFetchComplete();
           }

           @Override
           public void onBusinessDoesntExist() {
               businessNearbyListener.onBusinessDoesntExist();
           }
       };

        loadedBusinesses.clear();

        // Start Firebase Service
        firebaseBusinessNearbyService = FirebaseBusinessNearbyService.getInstance();
        firebaseBusinessNearbyService.startNearbyService(longitude, latitude, businessNearbyListener1);


        // Start Places API Service
        googlePlacesNearbyService = GooglePlacesNearbyService.getInstance(context, fragmentActivity, onConnectionFailedListener);
        googlePlacesNearbyService.startNearbyService(longitude, latitude, businessNearbyListener1);


        // Start Wifi Service
        //...


    }

    public void stopNearbyListeners(){
        if(firebaseBusinessNearbyService != null) {
            firebaseBusinessNearbyService.stopNearbyListerner();
        }
    }

    // Return Instance
    public static BusinessNearbyService getInstance() {
        if(instance == null)
            instance = new BusinessNearbyService();

        return instance;
    }

    // Helpers


    // Getters


    // Setters

}

package com.meetup.uhoo.service_layer.business_services;

import android.content.Context;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.common.api.GoogleApiClient;

/**
 * Created by sultankhan on 1/25/17.
 */
public class BusinessNearbyService {

    private FirebaseBusinessNearbyService firebaseBusinessNearbyService;
    private GooglePlacesNearbyService googlePlacesNearbyService;
    private static BusinessNearbyService instance;

    public BusinessNearbyService(){

    }

    // Public
    public void startNearbyService(Double longitude, Double latitude, BusinessNearbyListener businessNearbyListener, Context context, FragmentActivity fragmentActivity, GoogleApiClient.OnConnectionFailedListener onConnectionFailedListener ){


        // Start Firebase Service
        firebaseBusinessNearbyService = FirebaseBusinessNearbyService.getInstance();
        firebaseBusinessNearbyService.startNearbyService(longitude, latitude, businessNearbyListener);


        // Start Places API Service
        googlePlacesNearbyService = GooglePlacesNearbyService.getInstance(context, fragmentActivity, onConnectionFailedListener);
        googlePlacesNearbyService.startNearbyService(longitude, latitude, businessNearbyListener);


        // Start Wifi Service
        //...


    }

    public void stopNearbyListeners(){
        firebaseBusinessNearbyService.stopNearbyListerner();
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
package com.meetup.uhoo.service_layer.business_nearby;

/**
 * Created by sultankhan on 1/25/17.
 */
public class BusinessNearbyService {

    private FirebaseBusinessNearbyService firebaseBusinessNearbyService;
    private static BusinessNearbyService instance;

    public BusinessNearbyService(){

    }

    // Public
    public void startNearbyService( Double longitude, Double latitude, BusinessNearbyListener businessNearbyListener ){


        // Start Firebase Service
        firebaseBusinessNearbyService = FirebaseBusinessNearbyService.getInstance();
        firebaseBusinessNearbyService.startNearbyService(longitude, latitude, businessNearbyListener);


        // Start Places API Service
        //...


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

package com.meetup.uhoo.service_layer.business_services;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBuffer;
import com.google.android.gms.location.places.Places;
import com.meetup.uhoo.core.Business;

import java.util.ArrayList;

/**
 * Created by sultankhan on 1/25/17.
 */
public class GooglePlacesNearbyService {

    private static GooglePlacesNearbyService instance;
    private GoogleApiClient mGoogleApiClient;



    public GooglePlacesNearbyService(Context context, FragmentActivity fragmentActivity, GoogleApiClient.OnConnectionFailedListener onConnectionFailedListener){
        mGoogleApiClient = new GoogleApiClient
                .Builder(context)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(fragmentActivity, onConnectionFailedListener)
                .build();
    }


    // Public
    public void startNearbyService(Double longitude, Double latitude, final BusinessNearbyListener businessNearbyListener ){

        // TODO: Security Permission will crash app if user doesnt allow location
        // Query Nearby Locations and populate spinner
        PendingResult<PlaceLikelihoodBuffer> result = Places.PlaceDetectionApi.getCurrentPlace(mGoogleApiClient, null);
        result.setResultCallback(new ResultCallback<PlaceLikelihoodBuffer>() {
            @Override
            public void onResult(PlaceLikelihoodBuffer likelyPlaces) {

                //TODO: get rid of limit
                int limit = 5;

                for (final PlaceLikelihood placeLikelihood : likelyPlaces) {

                    if (limit <= 0) {
                        break;
                    }

                    /* //TODO: uncomment this to limit only cafes
                    // Limit to only cafes
                    if(placeLikelihood.getPlace().getPlaceTypes().contains(Place.TYPE_CAFE)) {
                        Log.i("places", String.format("Place '%s' has likelihood: %g",
                                placeLikelihood.getPlace().getName(),
                                placeLikelihood.getLikelihood()));

                        placesData.add(new Business(placeLikelihood.getPlace()));
                    }
                    */

                    // No Limit to business type
                    Log.i("places", String.format("Place '%s' has likelihood: %g",
                            placeLikelihood.getPlace().getName(),
                            placeLikelihood.getLikelihood()));

                    final Business tempBusiness = new Business(placeLikelihood.getPlace());

                    BusinessService businessService = new BusinessService();
                    businessService.fetchBusiness(placeLikelihood.getPlace().getId(), new BusinessNearbyListener() {
                        @Override
                        public void onBusinessFetched(Business object) {
                            businessNearbyListener.onBusinessFetched(object);
                        }

                        @Override
                        public void onFetchComplete(ArrayList<Business> loadedBusinesses) {
                            businessNearbyListener.onFetchComplete();
                        }

                        @Override
                        public void onFetchComplete() {
                            businessNearbyListener.onFetchComplete();
                        }

                        @Override
                        public void onBusinessDoesntExist() {
                            businessNearbyListener.onBusinessFetched(tempBusiness);
                        }
                    });

                    limit--;
                }
                likelyPlaces.release();



            }
        });


    }


    // Return Instance
    public static GooglePlacesNearbyService getInstance(Context context, FragmentActivity fragmentActivity, GoogleApiClient.OnConnectionFailedListener onConnectionFailedListener) {
        if(instance == null)
            instance = new GooglePlacesNearbyService(context, fragmentActivity, onConnectionFailedListener);

        return instance;
    }

    // Helpers


    // Getters


    // Setters


}

package com.meetup.uhoo.service_layer.auto_checkout_services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.meetup.uhoo.service_layer.user_services.CurrentUserDataService;
import com.meetup.uhoo.service_layer.user_services.UserCheckinListener;
import com.meetup.uhoo.service_layer.user_services.UserCheckinService;
import com.meetup.uhoo.util.location.FallbackLocationTracker;
import com.meetup.uhoo.util.location.LocationTracker;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by sultankhan on 3/25/17.
 */
public class AutoCheckoutService extends Service implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    Double longitude;
    Double latitude;
    String businessId;

    ScheduledExecutorService scheduleTaskExecutor;

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }


    @Override
    public void onDestroy() {
        mGoogleApiClient.disconnect();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("AutoCheckout", "autoCheckout Started");


        if (intent != null && intent.getExtras() != null) {
            //Log.i("AutoCheckout", "Got Location Intent");
            businessId = intent.getExtras().getString("businessId");
            longitude = intent.getExtras().getDouble("longitude");
            latitude = intent.getExtras().getDouble("latitude");
        }


        scheduleTaskExecutor = Executors.newScheduledThreadPool(5);
        // This schedule a runnable task every X minutes
        scheduleTaskExecutor.scheduleAtFixedRate(new Runnable() {
            public void run() {
                Log.i("AutoCheckout", "ping");

                // Connect the client.
                mGoogleApiClient.connect();


            }
        }, 0, 20, TimeUnit.MINUTES);


        return Service.START_STICKY;

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        //mGoogleApiClient.connect();
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(1000); // Update location every second

        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        Log.i("AutoCheckoutService", "Location received: " + location.toString());
                        Log.i("AutoCheckoutService", "User Location: " + latitude);

                        Location loc1 = new Location("");
                        loc1.setLatitude(latitude);
                        loc1.setLongitude(longitude);

                        Location loc2 = new Location("");
                        loc2.setLatitude(location.getLatitude());
                        loc2.setLongitude(location.getLongitude());

                        float distanceInMeters = loc1.distanceTo(loc2);
                        Log.i("AutoCheckoutService", "User Distance: " + distanceInMeters);

                        if(distanceInMeters > 50){
                            Log.i("AutoCheckoutService", "Distance over 150 meters");
                            UserCheckinService userCheckinService = new UserCheckinService(getApplicationContext(), FirebaseAuth.getInstance().getCurrentUser().getUid());
                            userCheckinService.checkoutUser(FirebaseAuth.getInstance().getCurrentUser().getUid(), businessId, new UserCheckinListener() {
                                @Override
                                public void onUserCheckinStateChange(boolean isCheckedIn) {
                                    if(!isCheckedIn){
                                        Log.i("AutoCheckoutService", "User Checked Out");
                                        scheduleTaskExecutor.shutdown();
                                        stopSelf();
                                    }
                                }
                            });
                        }

                        mGoogleApiClient.disconnect();
                    }
                });

    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i("AutoCheckoutService", "GoogleApiClient connection has been suspend");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i("AutoCheckoutService", "GoogleApiClient connection has failed");
    }


}

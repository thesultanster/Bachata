package com.meetup.uhoo.service_layer.auto_checkin_services;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.wifi.ScanResult;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.meetup.uhoo.core.Business;
import com.meetup.uhoo.core.User;
import com.meetup.uhoo.core.UserDataFetchListener;
import com.meetup.uhoo.restaurant.RestaurantActivity;
import com.meetup.uhoo.service_layer.auto_checkout_services.AutoCheckoutService;
import com.meetup.uhoo.service_layer.business_services.BusinessNearbyListener;
import com.meetup.uhoo.service_layer.business_services.BusinessService;
import com.meetup.uhoo.service_layer.user_services.CurrentUserDataService;
import com.meetup.uhoo.service_layer.user_services.UserCheckinListener;
import com.meetup.uhoo.service_layer.user_services.UserCheckinService;
import com.meetup.uhoo.service_layer.user_services.UserDataService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by sultankhan on 1/31/17.
 */
public class AutoCheckinService extends Service {

    private BroadcastReceiver awaitIPAddress = null;
    private WifiConnectionListener wifiConnectionListener;
    private String uid;
    Double longitude;
    Double latitude;
    String businessId;


    private final BroadcastReceiver onWifiConnectReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (action.equals(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION)) {
                if (intent.getParcelableExtra(WifiManager.EXTRA_NEW_STATE) == SupplicantState.COMPLETED) {
                    Log.i("AutoCheckinService", "onReceive: Wifi is associated");

                    //WiFi is associated
                    WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
                    WifiInfo wi = wifiManager.getConnectionInfo();
                    if (wi != null) {
                        Log.i("AutoCheckinService", "onReceive: Wifi info available (should be, we are associated)");

                        if (wi.getIpAddress() != 0) {
                            // Lucky us, we already have an ip address.
                            // This happens when a connection is complete

                            wifiConnectionListener.onWifiNetworkConnected(wi.getBSSID());
                            Log.i("AutoCheckinService", "onReceive:AwaitIPAddress: getBSSID: " + wi.getBSSID());

                        } else {
                            Log.i("AutoCheckinService", "onReceive: No ip address yet, we need to wait.");

                            // Battery friendly method, using events
                            if (awaitIPAddress == null) {
                                awaitIPAddress = new BroadcastReceiver() {
                                    @Override
                                    public void onReceive(Context ctx, Intent in) {
                                        WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
                                        WifiInfo wi = wifiManager.getConnectionInfo();
                                        if (wi != null) {
                                            if (wi.getIpAddress() != 0) {

                                                wifiConnectionListener.onWifiNetworkConnected(wi.getBSSID());
                                                Log.i("AutoCheckinService", "onReceive:AwaitIPAddress: getBSSID: " + wi.getBSSID());

                                            }
                                        } else {
                                            ctx.unregisterReceiver(this);
                                            awaitIPAddress = null;
                                        }
                                    }
                                };
                                // We register a new receiver for connectivity events
                                // (getting a new IP address for example)
                                context.registerReceiver(awaitIPAddress, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
                            }
                        }
                    }
                } else {
                    Log.i("AutoCheckinService", "onReceive: wifi connection not complete, release ip address receiver if registered");
                    // wifi connection not complete, release ip address receiver if registered
                    if (awaitIPAddress != null) {
                        context.unregisterReceiver(awaitIPAddress);
                        awaitIPAddress = null;
                    }
                }
            }
        }
    };


    @Override
    public void onCreate() {
        super.onCreate();


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        if (intent != null && intent.getExtras() != null) {
            //Log.i("AutoCheckout","Got Location Intent");
            longitude = intent.getExtras().getDouble("longitude");
            latitude = intent.getExtras().getDouble("latitude");
        }

        final String uid;
        try {
            uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        } catch (Exception e) {
            return Service.START_STICKY;
        }


        wifiConnectionListener = new WifiConnectionListener() {
            @Override
            public void onWifiNetworkConnected(String BSSID) {
                Log.i("wifiConnectionListener", "onWifiNetworkConnected:BSSID: " + BSSID);


                final CurrentUserDataService currentUser = new CurrentUserDataService(getApplicationContext());

                //  Check If BSSID is related to any businesses
                String formateedBSSID = BSSID.replace(":", "_");

                // Check if BSSID is related to any business on the database
                DatabaseReference restaurantsRef = FirebaseDatabase.getInstance().getReference();
                restaurantsRef.child("bssid_business_relation").child(formateedBSSID).addListenerForSingleValueEvent(
                        new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                // If does not exist then no relation between BSSID and businessId
                                if (!dataSnapshot.exists()) {
                                    Log.i("wifiConnectionListener", "bssid_business_relation:No Business ID Exists: ");
                                    return;
                                }

                                Log.i("wifiConnectionListener", "bssid_business_relation:BusinessID: " + dataSnapshot.getValue().toString());
                                businessId = dataSnapshot.getValue().toString();


                                // Checkin User
                                UserCheckinService userCheckinService = new UserCheckinService(getApplicationContext(), uid);
                                userCheckinService.checkin(businessId, uid);
                                currentUser.setCheckedInto(businessId);
                                currentUser.saveUserDataLocally(getApplicationContext());

                                Intent i = new Intent(getApplicationContext(), AutoCheckoutService.class);
                                i.putExtra("latitude", latitude);
                                i.putExtra("longitude", longitude);
                                i.putExtra("businessId", businessId);
                                startService(i);


                                BusinessService businessService = new BusinessService();
                                businessService.fetchBusiness(businessId, new BusinessNearbyListener() {
                                    @Override
                                    public void onBusinessFetched(Business object) {
                                        Intent intent = new Intent(getApplicationContext(), RestaurantActivity.class);
                                        intent.putExtra("business", object);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                    }

                                    @Override
                                    public void onFetchComplete() {

                                    }

                                    @Override
                                    public void onBusinessDoesntExist() {

                                    }
                                });


                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Log.e("onWifiNetworkConnected", "onWifiNetworkConnected:bssid_business_relation:onCancelled", databaseError.toException());
                            }
                        });
            }

        };

        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);
        registerReceiver(onWifiConnectReceiver, filter);


        return Service.START_STICKY;
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}

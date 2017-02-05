package com.meetup.uhoo.service_layer.user_services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.meetup.uhoo.AppConstant;
import com.meetup.uhoo.R;
import com.meetup.uhoo.core.Business;
import com.meetup.uhoo.core.User;
import com.meetup.uhoo.core.UserDataFetchListener;
import com.meetup.uhoo.service_layer.business_services.BusinessNearbyListener;
import com.meetup.uhoo.service_layer.business_services.BusinessService;

/**
 * Created by sultankhan on 1/31/17.
 */
public class UserCheckinService {

    private String uid;
    private UserCheckinListener userCheckinListener;
    private ValueEventListener postListener;
    private DatabaseReference userRef;
    private Context context;

    public UserCheckinService(Context context, String uid) {
        this.uid = uid;
        this.context = context;
    }

    public void startCheckinListener(final UserCheckinListener userCheckinListener) {
        this.userCheckinListener = userCheckinListener;

        // If one exists, remove it first
        stopCheckinListener();

        // Listener for Current User
        postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);

                Log.i("UserCheckinService", "startCheckinListener: " + user.isCheckedIn);

                // If user is checked in
                if (user != null && user.isCheckedIn) {
                    userCheckinListener.onUserCheckinStateChange(true);
                } else {
                    userCheckinListener.onUserCheckinStateChange(false);
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w("UserCheckinService", "startCheckinListener:onCancelled", databaseError.toException());
            }
        };
        userRef = FirebaseDatabase.getInstance().getReference("users/" + uid);
        userRef.addValueEventListener(postListener);
    }


    public void stopCheckinListener() {
        if (postListener != null) {
            userRef.removeEventListener(postListener);
        }
    }


    public void checkin(final String businessId, final String uid) {

        // Get Current User
        UserDataService userDataService = new UserDataService(uid);
        userDataService.getFirebaseUserData(new UserDataFetchListener() {
            @Override
            public void onUserFetch(final User user) {
                Log.i("UserCheckinService", "checkin:IsCheckedIn " + user.isCheckedIn);

                // If the user is already checked in
                if (user.isCheckedIn) {

                    checkout(user, new UserCheckinListener() {
                        @Override
                        public void onUserCheckinStateChange(boolean isCheckedIn) {
                            Log.i("UserCheckinService", "onUserCheckinStateChange:isCheckedIn " + user.isCheckedIn);

                            // Confirm user is checked out
                            if (!isCheckedIn) {

                                // Now check them into this business
                                checkinUser(businessId, uid);
                            }
                        }
                    });


                } else {

                    checkinUser(businessId, uid);
                }
            }
        });


    }


    public void checkout(User user) {

        // Create database reference
        // We will use this multiple times to update values to check out user
        DatabaseReference mDatabase;

        // Locally save user state as not checked into anything
        //SharedPreferences.Editor editor = getSharedPreferences("currentUser", MODE_PRIVATE).edit();
        //editor.putString("checkedInto", "");
        //editor.putString("checkedIntoBusiness", null);
        //editor.apply();

        // Remove user from checkin table on database
        mDatabase = FirebaseDatabase.getInstance().getReference("checkin").child(user.checkedInto);
        mDatabase.child(uid).removeValue();

        // Update user check in state to false on database
        mDatabase = FirebaseDatabase.getInstance().getReference("users").child(uid);
        mDatabase.child("isCheckedIn").setValue(false);

        NotificationManager nMgr = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        nMgr.cancel(AppConstant.CHECKIN_NOTIF);
    }


    public void checkout(User user, final UserCheckinListener userCheckinListener) {

        // Create database reference
        // We will use this multiple times to update values to check out user
        DatabaseReference mDatabase;

        // Locally save user state as not checked into anything
        SharedPreferences.Editor editor = context.getSharedPreferences("currentUser", context.MODE_PRIVATE).edit();
        editor.putString("checkedInto", "");
        //editor.putString("checkedIntoBusiness", null);
        editor.apply();

        // Remove user from checkin table on database
        mDatabase = FirebaseDatabase.getInstance().getReference("checkin").child(user.checkedInto);
        mDatabase.child(uid).removeValue(new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                // Update user check in state to false on database
                DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("users").child(uid);
                mDatabase.child("isCheckedIn").setValue(false).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        NotificationManager nMgr = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                        nMgr.cancel(AppConstant.CHECKIN_NOTIF);

                        userCheckinListener.onUserCheckinStateChange(false);
                    }
                });
            }
        });


    }


    // Helpers
    private void checkinUser(String businessId, String uid) {
        Log.i("UserCheckinService", "checkinUser");

        DatabaseReference mDatabase;

        // Add user to checkin table
        mDatabase = FirebaseDatabase.getInstance().getReference("checkin").child(businessId);
        mDatabase.child(uid).setValue(true);

        // Update user isCheckedIn state
        mDatabase = FirebaseDatabase.getInstance().getReference("users").child(uid);
        mDatabase.child("isCheckedIn").setValue(true);
        mDatabase.child("checkedInto").setValue(businessId);


        // Save placeId of checked in business locally
        //Gson gson = new Gson();
        // String json = gson.toJson(business);
        SharedPreferences.Editor editor = context.getSharedPreferences("currentUser", context.MODE_PRIVATE).edit();
        editor.putString("checkedInto", businessId);
        //editor.putString("checkedIntoBusiness", json);
        editor.apply();


        buildNotification(businessId);


    }

    private void buildNotification(String businessId){

        BusinessService businessService = new BusinessService();
        businessService.fetchBusiness(businessId, new BusinessNearbyListener() {
            @Override
            public void onBusinessFetched(Business object) {
                // Create Notifications
                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
                mBuilder.setSmallIcon(R.mipmap.uhoo_icon);
                mBuilder.setContentTitle("Checked into " + object.getName());
                mBuilder.setContentText("You are currently checked into this business");
                mBuilder.setOngoing(true);
                NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

                // Create Broadcast Intent for Deals button
                Intent dealsReceive = new Intent();
                dealsReceive.setAction(AppConstant.DEALS_ACTION);
                PendingIntent pendingIntentYes = PendingIntent.getBroadcast(context, 12345, dealsReceive, PendingIntent.FLAG_UPDATE_CURRENT);
                //mBuilder.addAction(R.mipmap.gps_refresh_icon, "Happenings", pendingIntentYes);

                // Create Broadcast Intent for Checkout button
                Intent chekoutReceive = new Intent();
                chekoutReceive.setAction(AppConstant.CHECKOUT_ACTION);
                PendingIntent pendingIntentYes2 = PendingIntent.getBroadcast(context, 12345, chekoutReceive, PendingIntent.FLAG_UPDATE_CURRENT);
                mBuilder.addAction(R.mipmap.x_grey, "Check Out", pendingIntentYes2);

                // notificationID allows you to update the notification later on.
                mNotificationManager.notify(AppConstant.CHECKIN_NOTIF, mBuilder.build());
            }

            @Override
            public void onFetchComplete() {

            }
        });



    }

    // Getters


    // Setters


}

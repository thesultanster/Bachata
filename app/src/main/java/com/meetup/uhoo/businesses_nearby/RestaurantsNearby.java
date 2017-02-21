package com.meetup.uhoo.businesses_nearby;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;


import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.meetup.uhoo.AppConstant;
import com.meetup.uhoo.core.Business;
import com.meetup.uhoo.service_layer.business_services.BusinessNearbyListener;
import com.meetup.uhoo.service_layer.business_services.BusinessNearbyService;
import com.meetup.uhoo.views.CheckinProfileDetailsView;
import com.meetup.uhoo.R;
import com.meetup.uhoo.core.User;
import com.meetup.uhoo.restaurant.RestaurantActivity;
import com.meetup.uhoo.util.NavigationDrawerFramework;

import java.util.ArrayList;

public class RestaurantsNearby extends NavigationDrawerFramework implements GoogleApiClient.OnConnectionFailedListener {

    // Used to manually update list of nearby users
    SwipeRefreshLayout mSwipeRefreshLayout;
    // List View to show nearby users
    RecyclerView recyclerView;
    // RecyclerView adapter to add/remove rows
    RestaurantsNearbyRecyclerAdapter adapter;
    ViewSwitcher viewSwitcher;

    ArrayList<Business> placesData;


    FloatingActionButton fabCheckinCheckout;
    TextView tvCehckinFABLabel;
    TextView tvCheckinText;
    NestedScrollView nsvBottomSheet;

    LinearLayout llCheckedInBusinessDetails;
    CheckinProfileDetailsView cpdProfileDetailView;
    TextView tvMoreInfoBusiness;
    TextView tvBusinessName;
    TextView tvBusinessCheckins;
    TextView tvBusinessHappenings;

    BusinessNearbyService businessNearbyService;


    User user;
    Boolean userLoadFired = false;

    Business checkedInBusiness;


    GoogleApiClient mGoogleApiClient;
    final DatabaseReference ref = FirebaseDatabase.getInstance().getReference("restaurant_locations");
    DatabaseReference userRef;
    DatabaseReference checkedInBusinessRef;
    final GeoFire geoFire = new GeoFire(ref);
    GeoQuery geoQuery;
    ValueEventListener postListener;
    ValueEventListener checkedInBusinessListener;

    CheckinCafesNearbyDialog checkinDialog;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_businesses_nearby);



        // Set Toolbar title
        getToolbar().setTitle("Meet People");

        // Set Up Variables
        InflateVariables();


        GetNearbyBusinesses();


        setOnClickListensers();


    }


    private void InflateVariables() {
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        adapter = new RestaurantsNearbyRecyclerAdapter(RestaurantsNearby.this, new ArrayList<Business>());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Refresh();
            }
        });

        fabCheckinCheckout = (FloatingActionButton) findViewById(R.id.fbCheckinCheckout);
        tvCehckinFABLabel = (TextView) findViewById(R.id.tvCheckinFABLabel);
        tvCheckinText = (TextView) findViewById(R.id.tvCheckinText);
        tvBusinessName = (TextView) findViewById(R.id.tvBusinessName);
        tvBusinessHappenings = (TextView) findViewById(R.id.tvBusinessHappenings);
        tvBusinessCheckins = (TextView) findViewById(R.id.tvBusinessCheckins);
        tvMoreInfoBusiness = (TextView) findViewById(R.id.tvMoreInfoBusiness);
        llCheckedInBusinessDetails = (LinearLayout) findViewById(R.id.llCheckedInBusinessDetails);
        cpdProfileDetailView = (CheckinProfileDetailsView) findViewById(R.id.cpdProfileDetailView);
        nsvBottomSheet = (NestedScrollView) findViewById(R.id.nsvBottomSheet);


        // Get user auth type. If anon user then Hide Profile Details
        SharedPreferences prefs = getSharedPreferences("currentUser", MODE_PRIVATE);
        String authType = prefs.getString("authType", null);
        if (authType != null && authType.equals("ANON")) {
            cpdProfileDetailView.setVisibility(View.GONE);
        }


    }

    private void setOnClickListensers() {
        tvMoreInfoBusiness.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), RestaurantActivity.class);
                intent.putExtra("business", checkedInBusiness);
                startActivity(intent);
            }
        });

        fabCheckinCheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Get user auth type. If anon user then tell them to create an account
                SharedPreferences prefs = getSharedPreferences("currentUser", MODE_PRIVATE);
                String authType = prefs.getString("authType", null);
                if (authType != null && authType.equals("ANON")) {
                    Toast.makeText(RestaurantsNearby.this, "Please Create An Account", Toast.LENGTH_SHORT).show();
                    return;
                }

                // If the user is not already checked in
                // Check them in
                if (!user.isCheckedIn) {


                    // Create Custom Checkin Dialog
                    // Implement rowclick functionality
                    checkinDialog = new CheckinCafesNearbyDialog(RestaurantsNearby.this, placesData, new CheckinCafesNearbyViewHolderClicks() {
                        @Override
                        public void rowClick(View caller, int position) {

                            // Generate business object for logging purposes
                            Business place = placesData.get(position);
                            // Log result
                            Log.i("Business selected", place.getName());


                            // Set Business Data in Checkin bottom sheet
                            tvBusinessName.setText(place.getName());

                            CheckInUser(position);
                        }
                    });

                    checkinDialog.setContentView(R.layout.dialog_checkin_nearest_cafes);
                    checkinDialog.show();

                }
                // If the user is already checked in
                // Check them out
                else {
                    CheckOutUser();
                }


            }
        });


    }

    void GetNearbyBusinesses() {

        businessNearbyService =  BusinessNearbyService.getInstance();

    }


    private void Refresh() {
        if (user == null || adapter == null ) {
            Log.d("refresh", "user not loaded");
            mSwipeRefreshLayout.setRefreshing(false);
            return;
        }


        adapter.clearData();



        businessNearbyService.startNearbyService(user.longitude, user.latitude, new BusinessNearbyListener() {
            @Override
            public void onBusinessFetched(Business object) {
                Log.i("businessNearbyService", "onBusinessFetched: " + object.getName());
                adapter.addRow(object);
            }

            @Override
            public void onFetchComplete() {
                mSwipeRefreshLayout.setRefreshing(false);

            }

            @Override
            public void onBusinessDoesntExist() {

            }
        }, this, this, this);


    }


    @Override
    protected void onStop() {
        super.onStop();

        stopCurrentUserListener();
        stopCheckinBusinessListener();

        businessNearbyService.stopNearbyListeners();

    }

    @Override
    public void onStart() {
        super.onStart();

        startCurrentUserListener();
        startCheckedInBusinessListener();

        Refresh();

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        mSwipeRefreshLayout.setRefreshing(false);
    }

    private void startCurrentUserListener() {
        // If one exists, remove it first
        stopCurrentUserListener();

        // Listener for Current User
        postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                user = dataSnapshot.getValue(User.class);
                Log.i("user", "isCheckedIn " + user.isCheckedIn);

                // If user is checked in
                if (user != null && user.isCheckedIn) {
                    tvCheckinText.setText("Checked In");
                    tvCheckinText.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.green_pill));
                    tvCehckinFABLabel.setVisibility(View.VISIBLE);
                    fabCheckinCheckout.setVisibility(View.VISIBLE);

                    // Show business details in bottom sheet
                    llCheckedInBusinessDetails.setVisibility(View.VISIBLE);
                    startCheckedInBusinessListener();

                } else {
                    tvCheckinText.setText("Not Checked In");
                    tvCheckinText.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.red_pill));
                    tvCehckinFABLabel.setVisibility(View.GONE);
                    tvBusinessName.setText("Select a business!");
                    fabCheckinCheckout.setVisibility(View.GONE);

                    // Hide business  details in bottom sheet
                    llCheckedInBusinessDetails.setVisibility(View.GONE);
                    stopCheckinBusinessListener();
                }

                // Load Refresh users when user is once loaded
                // This is to stop updating the list at every user update
                // Because we are inside an onDataChange method, it will be fired
                // Everytime a user is updated. We just wanna load list once
                if (!userLoadFired) {
                    Log.d("user", "load fired");
                    Refresh();
                    userLoadFired = true;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w("user", "loadPost:onCancelled", databaseError.toException());
            }
        };
        userRef = FirebaseDatabase.getInstance().getReference("users/" + FirebaseAuth.getInstance().getCurrentUser().getUid());
        userRef.addValueEventListener(postListener);
    }

    private void stopCurrentUserListener() {
        if (postListener != null) {
            userRef.removeEventListener(postListener);
        }
    }


    private void startCheckedInBusinessListener() {
        // If one exists, remove it first
        stopCheckinBusinessListener();

        // Check what business user is checked in
        SharedPreferences prefs = getSharedPreferences("currentUser", MODE_PRIVATE);
        String checkedIntoBusinessId = prefs.getString("checkedInto", "");

        if (!checkedIntoBusinessId.equals("")) {


            // Listener for Current User
            checkedInBusinessListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    checkedInBusiness = dataSnapshot.getValue(Business.class);
                    Log.i("business", "onDataChange");

                    tvBusinessCheckins.setText(checkedInBusiness.getNumUsersCheckedIn() + "");
                    tvBusinessHappenings.setText(checkedInBusiness.getNumHappenings() + "");
                    tvBusinessName.setText(checkedInBusiness.getName());

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Getting Business failed, log a message
                    Log.e("business", "onCancelled", databaseError.toException());
                }
            };
            checkedInBusinessRef = FirebaseDatabase.getInstance().getReference("restaurants/" + checkedIntoBusinessId);
            checkedInBusinessRef.addValueEventListener(checkedInBusinessListener);
        }
    }

    private void stopCheckinBusinessListener() {
        if (checkedInBusinessListener != null) {
            checkedInBusinessRef.removeEventListener(checkedInBusinessListener);
        }
    }


    private void CheckOutUser() {

        // Create database reference
        // We will use this multiple times to update values to check out user
        DatabaseReference mDatabase;

        // Locally save user state as not checked into anything
        SharedPreferences.Editor editor = getSharedPreferences("currentUser", MODE_PRIVATE).edit();
        editor.putString("checkedInto", "");
        editor.putString("checkedIntoBusiness", null);
        editor.apply();

        // Remove user from checkin table on database
        mDatabase = FirebaseDatabase.getInstance().getReference("checkin").child(user.checkedInto);
        mDatabase.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).removeValue();

        // Update user check in state to false on database
        mDatabase = FirebaseDatabase.getInstance().getReference("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        mDatabase.child("isCheckedIn").setValue(false);



        Refresh();
    }


    private void CheckInUser(final int businessPosition) {

        // If user data has not been loaded
        // Don't do anything
        if (user == null) {
            Log.d("user", "data not loaded yet");
            return;
        }

        // Get user auth type. If anon user then tell them to create an account
        SharedPreferences prefs = getSharedPreferences("currentUser", MODE_PRIVATE);
        String authType = prefs.getString("authType", null);
        if (authType != null && authType.equals("ANON")) {
            Toast.makeText(RestaurantsNearby.this, "Please Create An Account", Toast.LENGTH_SHORT).show();
            return;
        }


        // If User is not checked in anywhere
        if (!user.isCheckedIn) {

            // Get Selected Place object
            final Business place = placesData.get(businessPosition);

            // Add Place Id to GeoFire Table
            // If it exists already, then atleast it updates new LatLng if it is updated
            geoFire.setLocation(place.getPlaceId(), new GeoLocation(place.getLatitude(), place.getLongitude()), new GeoFire.CompletionListener() {
                @Override
                public void onComplete(String key, DatabaseError error) {
                    if (error != null) {
                        System.err.println("There was an error saving the location to GeoFire: " + error);
                    } else {
                        System.out.println("Location ID saved on server successfully!");


                        DatabaseReference mDatabase;

                        // Add user to checkin table
                        mDatabase = FirebaseDatabase.getInstance().getReference("checkin").child(place.getPlaceId());
                        mDatabase.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(true);

                        // Update user isCheckedIn state
                        mDatabase = FirebaseDatabase.getInstance().getReference("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                        mDatabase.child("isCheckedIn").setValue(true);
                        mDatabase.child("checkedInto").setValue(place.getPlaceId());

                        // Update restaurant info on restaurant table
                        mDatabase = FirebaseDatabase.getInstance().getReference("restaurants");
                        mDatabase.child(place.getPlaceId()).setValue(place);


                        // Save placeId of checked in business locally
                        Gson gson = new Gson();
                        String json = gson.toJson(placesData.get(businessPosition));
                        SharedPreferences.Editor editor = getSharedPreferences("currentUser", MODE_PRIVATE).edit();
                        editor.putString("checkedInto", place.getPlaceId());
                        editor.putString("checkedIntoBusiness", json);
                        editor.apply();


                        // Create Notifications
                        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext());
                        mBuilder.setSmallIcon(R.mipmap.uhoo_icon);
                        mBuilder.setContentTitle("Checked into " + place.getName());
                        mBuilder.setContentText("You are currently checked into this business");
                        mBuilder.setOngoing(true);
                        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                        // Create Broadcast Intent for Deals button
                        Intent dealsReceive = new Intent();
                        dealsReceive.setAction(AppConstant.DEALS_ACTION);
                        PendingIntent pendingIntentYes = PendingIntent.getBroadcast(getApplicationContext(), 12345, dealsReceive, PendingIntent.FLAG_UPDATE_CURRENT);
                        //mBuilder.addAction(R.mipmap.gps_refresh_icon, "Happenings", pendingIntentYes);

                        // Create Broadcast Intent for Checkout button
                        Intent chekoutReceive = new Intent();
                        chekoutReceive.setAction(AppConstant.CHECKOUT_ACTION);
                        PendingIntent pendingIntentYes2 = PendingIntent.getBroadcast(getApplicationContext(), 12345, chekoutReceive, PendingIntent.FLAG_UPDATE_CURRENT);
                        mBuilder.addAction(R.mipmap.x_grey, "Check Out", pendingIntentYes2);

                        // notificationID allows you to update the notification later on.
                        mNotificationManager.notify(AppConstant.CHECKIN_NOTIF, mBuilder.build());

                        // After locally saving business ID, start check in listenter
                        startCheckedInBusinessListener();



                        Refresh();

                        Intent intent = new Intent(getApplicationContext(), RestaurantActivity.class);
                        intent.putExtra("business", place);
                        startActivity(intent);

                    }
                }
            });


        }

        checkinDialog.dismiss();
    }
}

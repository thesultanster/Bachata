package com.meetup.uhoo.businesses_nearby;

import android.app.Dialog;
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
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;


import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBuffer;
import com.google.android.gms.location.places.PlaceTypes;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.vision.text.Text;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.meetup.uhoo.AppConstant;
import com.meetup.uhoo.Business;
import com.meetup.uhoo.R;
import com.meetup.uhoo.User;
import com.meetup.uhoo.UserDataFetchListener;
import com.meetup.uhoo.restaurant.RestaurantActivity;
import com.meetup.uhoo.util.NavigationDrawerFramework;

import java.util.ArrayList;
import java.util.List;

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

    TextView tvBusinessName;


    User user;
    Boolean userLoadFired = false;

    Business checkedInBusiness;


    GoogleApiClient mGoogleApiClient;
    final DatabaseReference ref = FirebaseDatabase.getInstance().getReference("restaurant_locations");
    DatabaseReference userRef;
    final GeoFire geoFire = new GeoFire(ref);
    GeoQuery geoQuery;
    ValueEventListener postListener;

    CheckinCafesNearbyDialog checkinDialog;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_businesses_nearby);

        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();


        // Set Toolbar title
        getToolbar().setTitle("Meet People");

        // Set Up Variables
        InflateVariables();


        GetNearbyBusinesses();


        fabCheckinCheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // If the user is not already checked in
                // Check them in
                if(!user.isCheckedIn){


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


    void InflateVariables() {
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        adapter = new RestaurantsNearbyRecyclerAdapter(RestaurantsNearby.this, new ArrayList<Business>());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        fabCheckinCheckout = (FloatingActionButton) findViewById(R.id.fbCheckinCheckout);
        tvCehckinFABLabel = (TextView) findViewById(R.id.tvCheckinFABLabel);
        tvCheckinText = (TextView) findViewById(R.id.tvCheckinText);
        tvBusinessName = (TextView) findViewById(R.id.tvBusinessName);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Refresh();
            }
        });



    }

    void GetNearbyBusinesses() {
        // TODO: Security Permission will crash app if user doesnt allow location
        // Query Nearby Locations and populate spinner
        PendingResult<PlaceLikelihoodBuffer> result = Places.PlaceDetectionApi.getCurrentPlace(mGoogleApiClient, null);
        result.setResultCallback(new ResultCallback<PlaceLikelihoodBuffer>() {
            @Override
            public void onResult(PlaceLikelihoodBuffer likelyPlaces) {

                placesData = new ArrayList<Business>();

                int limit = 5;

                for (PlaceLikelihood placeLikelihood : likelyPlaces) {

                    if (limit <= 0 ) {
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

                    placesData.add(new Business(placeLikelihood.getPlace()));

                    limit--;
                }
                likelyPlaces.release();


            }
        });
    }


    private void Refresh() {
        adapter.clearData();


        if (user == null) {
            Log.d("refresh", "user not loaded");
            mSwipeRefreshLayout.setRefreshing(false);
            return;
        }


        // Given Manual Location, query for changes in all object in a 0.6ki radius
        geoQuery = geoFire.queryAtLocation(new GeoLocation(user.latitude, user.longitude), 0.6);
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

                                                    adapter.addRow(restaurant);
                                                }

                                                @Override
                                                public void onCancelled(DatabaseError databaseError) {
                                                    Log.w("restaurant checkin", "getRestaurant:onCancelled", databaseError.toException());
                                                }
                                            });
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Log.w("restaurant", "getRestaurant:onCancelled", databaseError.toException());
                            }
                        });


            }

            @Override
            public void onKeyExited(String key) {
                System.out.println(String.format("Key %s is no longer in the search area", key));
            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {
                System.out.println(String.format("Key %s moved within the search area to [%f,%f]", key, location.latitude, location.longitude));
            }

            @Override
            public void onGeoQueryReady() {
                System.out.println("All initial data has been loaded and events have been fired!");
                mSwipeRefreshLayout.setRefreshing(false);


            }

            @Override
            public void onGeoQueryError(DatabaseError error) {
                System.err.println("There was an error with this query: " + error);
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });


        // Default state when refreshing is no businesses around
        //if (R.id.recyclerView == viewSwitcher.getNextView().getId()) {
        //viewSwitcher.showNext();
        //}

    }


    @Override
    protected void onStop() {
        super.onStop();

        if (postListener != null) {
            userRef.removeEventListener(postListener);
        }

        if (geoQuery != null)
            geoQuery.removeAllListeners();
    }

    @Override
    public void onStart() {
        super.onStart();

        // Listener for Current User
        postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                user = dataSnapshot.getValue(User.class);
                Log.d("user", "isCheckedIn " + user.isCheckedIn);

                if (user != null && user.isCheckedIn) {
                    tvCheckinText.setText("Checked In");
                    tvCheckinText.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.green_pill));
                    tvCehckinFABLabel.setText("CHECK OUT");
                    fabCheckinCheckout.setImageResource(R.mipmap.check_out);

                    // Get user auth type. If anon user then tell them to create an account
                    Gson gson = new Gson();
                    SharedPreferences prefs = getSharedPreferences("currentUser", MODE_PRIVATE);
                    String json = prefs.getString("checkedIntoBusiness", "");
                    if(!json.equals("")) {
                        Business CheckedInBusiness = gson.fromJson(json, Business.class);
                        if (CheckedInBusiness != null) {
                            tvBusinessName.setText(CheckedInBusiness.getName());
                        }
                    }

                } else {
                    tvCheckinText.setText("Not Checked In");
                    tvCheckinText.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.red_pill));
                    tvCehckinFABLabel.setText("CHECK IN");
                    tvBusinessName.setText("");
                    fabCheckinCheckout.setImageResource(R.mipmap.checkin_white);
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
                // ...
            }
        };
        userRef = FirebaseDatabase.getInstance().getReference("users/" + FirebaseAuth.getInstance().getCurrentUser().getUid());
        userRef.addValueEventListener(postListener);

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        mSwipeRefreshLayout.setRefreshing(false);
    }


    private void CheckOutUser(){

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

    }


    private void CheckInUser(final int businessPosition ){

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
            final Business place =  placesData.get(businessPosition);

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
                        mBuilder.setSmallIcon(R.mipmap.beer);
                        mBuilder.setContentTitle("Checked into " + place.getName());
                        mBuilder.setContentText("You are currently checked into this business");
                        mBuilder.setOngoing(true);
                        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                        // Create Broadcast Intent for Deals button
                        Intent dealsReceive = new Intent();
                        dealsReceive.setAction(AppConstant.DEALS_ACTION);
                        PendingIntent pendingIntentYes = PendingIntent.getBroadcast(getApplicationContext(), 12345, dealsReceive, PendingIntent.FLAG_UPDATE_CURRENT);
                        mBuilder.addAction(R.mipmap.gps_refresh_icon, "Deals", pendingIntentYes);

                        // Create Broadcast Intent for Checkout button
                        Intent chekoutReceive = new Intent();
                        chekoutReceive.setAction(AppConstant.CHECKOUT_ACTION);
                        PendingIntent pendingIntentYes2 = PendingIntent.getBroadcast(getApplicationContext(), 12345, chekoutReceive, PendingIntent.FLAG_UPDATE_CURRENT);
                        mBuilder.addAction(R.mipmap.gps_refresh_icon, "Check Out", pendingIntentYes2);

                        // notificationID allows you to update the notification later on.
                        mNotificationManager.notify(AppConstant.CHECKIN_NOTIF, mBuilder.build());


                        Refresh();

                    }
                }
            });


        }

        checkinDialog.dismiss();
    }
}

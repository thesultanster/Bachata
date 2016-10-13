package com.meetup.uhoo.businesses_nearby;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;


import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBuffer;
import com.google.android.gms.location.places.Places;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.meetup.uhoo.Business;
import com.meetup.uhoo.R;
import com.meetup.uhoo.User;
import com.meetup.uhoo.util.NavigationDrawerFramework;

import java.util.ArrayList;
import java.util.List;

public class RestaurantsNearby extends NavigationDrawerFramework implements GoogleApiClient.OnConnectionFailedListener {

    // Used to manually update list of nearby users
    SwipeRefreshLayout mSwipeRefreshLayout;

    // List View to show nearby users
    RecyclerView recyclerView;

    Button checkinButton;
    Spinner placesSpinner;
    SpinnerAdapter spinnerAdapter;

    // RecyclerView adapter to add/remove rows
    RestaurantsNearbyRecyclerAdapter adapter;
    User user;

    GoogleApiClient mGoogleApiClient;
    final DatabaseReference ref = FirebaseDatabase.getInstance().getReference("restaurant_locations");
    DatabaseReference userRef;
    final GeoFire geoFire = new GeoFire(ref);
    GeoQuery geoQuery;

    Boolean userLoadFired = false;


    ValueEventListener postListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_people_nearby);

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


        // Checking In and Out Logic
        checkinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                // If user data has not been loaded
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
                    final Business place = (Business) spinnerAdapter.getItem(placesSpinner.getSelectedItemPosition());

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


                                Refresh();
                            }
                        }
                    });

                }

                // Else if the user is already checked in
                else {
                    DatabaseReference mDatabase;

                    // Remove user to checkin table
                    mDatabase = FirebaseDatabase.getInstance().getReference("checkin").child(user.checkedInto);
                    mDatabase.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).removeValue();

                    // Update user isCheckedIn state
                    mDatabase = FirebaseDatabase.getInstance().getReference("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                    mDatabase.child("isCheckedIn").setValue(false);
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
        placesSpinner = (Spinner) findViewById(R.id.placesSpinner);
        checkinButton = (Button) findViewById(R.id.checkinButton);

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

                ArrayList<Business> placesData = new ArrayList<Business>();

                int limit = 10;

                for (PlaceLikelihood placeLikelihood : likelyPlaces) {

                    if (limit <= 0) {
                        break;
                    }

                    Log.i("places", String.format("Place '%s' has likelihood: %g",
                            placeLikelihood.getPlace().getName(),
                            placeLikelihood.getLikelihood()));
                    placesData.add(new Business(placeLikelihood.getPlace()));

                    limit--;
                }
                likelyPlaces.release();

                // Initialize the adapter sending the current context
                // Send the simple_spinner_item layout
                // And finally send the Users array (Your data)
                spinnerAdapter = new PlacesNearbySpinnerAdapter(RestaurantsNearby.this,
                        R.layout.row_places_nearby,
                        placesData) {
                };


                placesSpinner.setAdapter(spinnerAdapter); // Set the custom adapter to the spinner
                // What to do when restaurant is selected
                placesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view,
                                               int position, long id) {
                        // Here you get the current item (a User object) that is selected by its position
                        Business place = (Business) spinnerAdapter.getItem(position);
                        // Here you can do the action you want to...
                        Toast.makeText(RestaurantsNearby.this, "Name: " + place.getName(),
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapter) {
                    }
                });


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
                                    // TODO: Find a way to persist this data when user goes to RestaurantActivity
                                    DatabaseReference restaurantsRef = FirebaseDatabase.getInstance().getReference();
                                    restaurantsRef.child("checkin").child(restaurant.getPlaceId()).addListenerForSingleValueEvent(
                                            new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {

                                                    // Save users in list
                                                    List<User> users = new ArrayList<User>();
                                                    for (DataSnapshot user : dataSnapshot.getChildren()) {
                                                        users.add(new User(user.getKey()));
                                                    }

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


                //adapter.addRow(new PeopleNearbyRecyclerInfo(key));

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


    }


    @Override
    protected void onStop() {
        super.onStop();

        if(postListener != null){
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
                    checkinButton.setText("Check Out");
                    placesSpinner.setVisibility(View.GONE);
                } else {
                    checkinButton.setText("Checkin");
                    placesSpinner.setVisibility(View.VISIBLE);
                }

                // Load Refresh users when user is once loaded
                // This is to stop updating the list at every user update
                // Because we are inside an onDataChange method, it will be fired
                // Everytime a user is updated. We just wanna load list once
                if (!userLoadFired) {
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
}

package com.meetup.uhoo.people_nearby;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;


import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.firebase.geofire.LocationCallback;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.plus.People;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.meetup.uhoo.R;
import com.meetup.uhoo.credentials.User;
import com.meetup.uhoo.util.NavigationDrawerFramework;

import java.util.ArrayList;

public class PeopleNearby extends NavigationDrawerFramework implements GoogleApiClient.OnConnectionFailedListener {

    // Used to manually update list of nearby users
    SwipeRefreshLayout mSwipeRefreshLayout;

    // List View to show nearby users
    RecyclerView recyclerView;

    Button checkinButton;
    Spinner placesSpinner;
    SpinnerAdapter spinnerAdapter;

    // RecyclerView adapter to add/remove rows
    PeopleNearbyRecyclerAdapter adapter;
    User user;

    private GoogleApiClient mGoogleApiClient;
    final DatabaseReference ref = FirebaseDatabase.getInstance().getReference("restaurant_locations");
    DatabaseReference userRef;
    final GeoFire geoFire = new GeoFire(ref);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
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


        geoFire.getLocation(FirebaseAuth.getInstance().getCurrentUser().getUid(), new LocationCallback() {
            @Override
            public void onLocationResult(String key, GeoLocation location) {
                if (location != null) {
                    System.out.println(String.format("The location for key %s is [%f,%f]", key, location.latitude, location.longitude));

                    // Given Manual Location, query for changes in all object in a 0.6ki radius
                    GeoQuery geoQuery = geoFire.queryAtLocation(location, 0.6);
                    geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
                        @Override
                        public void onKeyEntered(String key, GeoLocation location) {
                            System.out.println(String.format("Key %s entered the search area at [%f,%f]", key, location.latitude, location.longitude));
                            adapter.addRow(new PeopleNearbyRecyclerInfo(key, key));
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
                        }

                        @Override
                        public void onGeoQueryError(DatabaseError error) {
                            System.err.println("There was an error with this query: " + error);
                        }
                    });

                } else {
                    System.out.println(String.format("There is no location for key %s in GeoFire", key));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.err.println("There was an error getting the GeoFire location: " + databaseError);
            }
        });


        // TODO: Security Permission will crash app if user doesnt allow location
        PendingResult<PlaceLikelihoodBuffer> result = Places.PlaceDetectionApi.getCurrentPlace(mGoogleApiClient, null);
        result.setResultCallback(new ResultCallback<PlaceLikelihoodBuffer>() {
            @Override
            public void onResult(PlaceLikelihoodBuffer likelyPlaces) {

                ArrayList<PlacesNearbySpinnerInfo> placesData = new ArrayList<PlacesNearbySpinnerInfo>();

                for (PlaceLikelihood placeLikelihood : likelyPlaces) {
                    Log.i("places", String.format("Place '%s' has likelihood: %g",
                            placeLikelihood.getPlace().getName(),
                            placeLikelihood.getLikelihood()));
                    placesData.add(new PlacesNearbySpinnerInfo(placeLikelihood.getPlace()));
                }
                likelyPlaces.release();

                // Initialize the adapter sending the current context
                // Send the simple_spinner_item layout
                // And finally send the Users array (Your data)
                spinnerAdapter = new PlacesNearbySpinnerAdapter(PeopleNearby.this,
                        android.R.layout.simple_spinner_item,
                        placesData) {
                };


                placesSpinner.setAdapter(spinnerAdapter); // Set the custom adapter to the spinner
                // You can create an anonymous listener to handle the event when is selected an spinner item
                placesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view,
                                               int position, long id) {
                        // Here you get the current item (a User object) that is selected by its position
                        PlacesNearbySpinnerInfo place = (PlacesNearbySpinnerInfo) spinnerAdapter.getItem(position);
                        // Here you can do the action you want to...
                        Toast.makeText(PeopleNearby.this, "Name: " + place.getName(),
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapter) {
                    }
                });


            }
        });


        checkinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // If user data has not been loaded
                if(user == null){
                    Log.d("user","data not loaded yet");
                    return;
                }

                if ( !user.isCheckedIn) {

                    final PlacesNearbySpinnerInfo place = (PlacesNearbySpinnerInfo) spinnerAdapter.getItem(placesSpinner.getSelectedItemPosition());
                    // Add Place Id to GeoFire Table
                    // If it exists already, then atleast it updates new LatLng if it is updated
                    geoFire.setLocation(place.getPlace().getId(), new GeoLocation(place.getLatitude(), place.getLongitude()), new GeoFire.CompletionListener() {
                        @Override
                        public void onComplete(String key, DatabaseError error) {
                            if (error != null) {
                                System.err.println("There was an error saving the location to GeoFire: " + error);
                            } else {
                                System.out.println("Location ID saved on server successfully!");


                                DatabaseReference mDatabase;

                                // Add user to checkin table
                                mDatabase = FirebaseDatabase.getInstance().getReference("checkin").child(place.getPlace().getId());
                                mDatabase.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(true);

                                // Update user isCheckedIn state
                                mDatabase = FirebaseDatabase.getInstance().getReference("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                                mDatabase.child("isCheckedIn").setValue(true);
                                mDatabase.child("checkedInto").setValue(place.getPlace().getId());

                            }
                        }
                    });

                }
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


        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                user = dataSnapshot.getValue(User.class);
                Log.d("user", "isCheckedIn" + user.isCheckedIn);

                if (user != null && user.isCheckedIn) {
                    checkinButton.setText("Check Out");
                    placesSpinner.setVisibility(View.GONE);
                } else {
                    checkinButton.setText("Checkin");
                    placesSpinner.setVisibility(View.VISIBLE);
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


    void InflateVariables() {
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        adapter = new PeopleNearbyRecyclerAdapter(PeopleNearby.this, new ArrayList<PeopleNearbyRecyclerInfo>());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        placesSpinner = (Spinner) findViewById(R.id.placesSpinner);
        checkinButton = (Button) findViewById(R.id.checkinButton);


    }


    private void Refresh() {
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        adapter = new PeopleNearbyRecyclerAdapter(PeopleNearby.this, new ArrayList<PeopleNearbyRecyclerInfo>());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(PeopleNearby.this));

        adapter.addRow(new PeopleNearbyRecyclerInfo("David Hasselhoff", "Product Manager"));
        adapter.addRow(new PeopleNearbyRecyclerInfo("Mark DeReyouter", "Develiper"));
        adapter.addRow(new PeopleNearbyRecyclerInfo("David Bowie", "Product Manager"));


    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}

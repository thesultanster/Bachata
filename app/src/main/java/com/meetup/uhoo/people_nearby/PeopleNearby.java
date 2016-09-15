package com.meetup.uhoo.people_nearby;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;


import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.firebase.geofire.LocationCallback;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.meetup.uhoo.R;
import com.meetup.uhoo.util.NavigationDrawerFramework;

import java.util.ArrayList;

public class PeopleNearby extends NavigationDrawerFramework {

    // Used to manually update list of nearby users
    SwipeRefreshLayout mSwipeRefreshLayout;

    // List View to show nearby users«
    RecyclerView recyclerView;

    // RecyclerView adapter to add/remove rows
    PeopleNearbyRecyclerAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_people_nearby);

        // Set Toolbar title
        getToolbar().setTitle("Meet People");

        // Set Up Variables
        InflateVariables();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("locations");
        final GeoFire geoFire = new GeoFire(ref);

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



    }




    void InflateVariables() {
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        adapter = new PeopleNearbyRecyclerAdapter(PeopleNearby.this, new ArrayList<PeopleNearbyRecyclerInfo>());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }


    private void Refresh() {
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        adapter = new PeopleNearbyRecyclerAdapter(PeopleNearby.this, new ArrayList<PeopleNearbyRecyclerInfo>());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(PeopleNearby.this));

        adapter.addRow(new PeopleNearbyRecyclerInfo("David Hasselhoff","Product Manager"));
        adapter.addRow(new PeopleNearbyRecyclerInfo("Mark DeReyouter","Develiper"));
        adapter.addRow(new PeopleNearbyRecyclerInfo("David Bowie","Product Manager"));


    }




}

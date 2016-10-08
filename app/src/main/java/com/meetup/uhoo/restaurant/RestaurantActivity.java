package com.meetup.uhoo.restaurant;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.meetup.uhoo.R;
import com.meetup.uhoo.Business;
import com.meetup.uhoo.User;

import java.util.ArrayList;

public class RestaurantActivity extends AppCompatActivity {


    // Used to manually update list of nearby users
    SwipeRefreshLayout mSwipeRefreshLayout;

    // List View to show nearby users
    RecyclerView recyclerView;
    PeopleNearbyRecyclerAdapter adapter;

    //String restaurantId;
    Business business;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant);

        // Get business details
        business = (Business) getIntent().getExtras().get("business");
        //restaurantId = getIntent().getExtras().getString("restaurantId");

        InflateVariables();


        // Set Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        try {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle(business.getName());
        } catch (Exception e) {
            Toast.makeText(RestaurantActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
        }


        // Load User keys that are checked into current Restaurant
        DatabaseReference restaurantsRef = FirebaseDatabase.getInstance().getReference();
        restaurantsRef.child("checkin").child(business.getId()).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        for (DataSnapshot user : dataSnapshot.getChildren()) {
                            adapter.addRow(new User(user.getKey()));
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w("restaurant checkin", "getRestaurant:onCancelled", databaseError.toException());
                    }
                });

    }

    void InflateVariables() {
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        adapter = new PeopleNearbyRecyclerAdapter(RestaurantActivity.this, new ArrayList<User>());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }
}

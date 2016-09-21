package com.meetup.uhoo.restaurant;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.meetup.uhoo.R;
import com.meetup.uhoo.credentials.User;
import com.meetup.uhoo.people_nearby.RestaurantsNearbyRecyclerAdapter;
import com.meetup.uhoo.people_nearby.RestaurantsNearbyRecyclerInfo;

import java.util.ArrayList;

public class RestaurantActivity extends AppCompatActivity {


    // Used to manually update list of nearby users
    SwipeRefreshLayout mSwipeRefreshLayout;

    // List View to show nearby users
    RecyclerView recyclerView;
    PeopleNearbyRecyclerAdapter adapter;

    String restaurantId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant);

        restaurantId = getIntent().getExtras().getString("restaurantId");

        InflateVariables();


        DatabaseReference restaurantsRef = FirebaseDatabase.getInstance().getReference();
        restaurantsRef.child("checkin").child(restaurantId).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        for(DataSnapshot user : dataSnapshot.getChildren()){
                            adapter.addRow(new PeopleNearbyRecyclerInfo(user.getKey().toString()));
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w("restaurant", "getRestaurant:onCancelled", databaseError.toException());
                    }
                });

    }

    void InflateVariables(){
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        adapter = new PeopleNearbyRecyclerAdapter(RestaurantActivity.this, new ArrayList<PeopleNearbyRecyclerInfo>());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }
}

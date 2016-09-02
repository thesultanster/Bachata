package com.meetup.uhoo.people_nearby;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;


import com.meetup.uhoo.R;

import java.util.ArrayList;

public class PeopleNearby extends AppCompatActivity {

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

        //getToolbar().setTitle("Teams");

        // Set Up Variables
        InflateVariables();

    }


    void InflateVariables() {
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        adapter = new PeopleNearbyRecyclerAdapter(PeopleNearby.this, new ArrayList<PeopleNearbyRecyclerInfo>());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);




        adapter.addRow(new PeopleNearbyRecyclerInfo("David Hasselhoff","Product Manager"));
        adapter.addRow(new PeopleNearbyRecyclerInfo("Mark DeReyouter","Developer"));
        adapter.addRow(new PeopleNearbyRecyclerInfo("David Bowie","Product Manager"));

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

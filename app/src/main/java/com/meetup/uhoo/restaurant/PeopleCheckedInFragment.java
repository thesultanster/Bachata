package com.meetup.uhoo.restaurant;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.meetup.uhoo.R;
import com.meetup.uhoo.core.User;

import java.util.ArrayList;

/**
 * Created by sultankhan on 11/22/16.
 */
public class PeopleCheckedInFragment extends Fragment {


    // List View to show nearby users
    private RecyclerView recyclerView;
    private PeopleNearbyRecyclerAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private String businessId;

    public PeopleCheckedInFragment( ) {
        this.businessId = businessId;
    }

    @SuppressLint("ValidFragment")
    public PeopleCheckedInFragment( String businessId) {
        this.businessId = businessId;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        adapter = new PeopleNearbyRecyclerAdapter(getActivity(), new ArrayList<User>());
        Refresh();


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_people_checkedin, container, false);


        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Refresh();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);


        return view;
    }

    public void Refresh(){
        adapter.clearData();

        // Load User keys that are checked into current Restaurant
        DatabaseReference restaurantsRef = FirebaseDatabase.getInstance().getReference();
        restaurantsRef.child("checkin").child(businessId).addListenerForSingleValueEvent(
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
}

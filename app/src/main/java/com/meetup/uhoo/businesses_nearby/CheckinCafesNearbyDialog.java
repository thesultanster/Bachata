package com.meetup.uhoo.businesses_nearby;

/**
 * Created by sultankhan on 11/13/16.
 */

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.meetup.uhoo.Business;
import com.meetup.uhoo.R;

import java.util.ArrayList;

class CheckinCafesNearbyDialog extends Dialog implements
        android.view.View.OnClickListener {

    public Activity c;
    public Dialog d;
    ArrayList<Business> nearbyCafes;
    // List View to show nearby users
    RecyclerView recyclerView;
    // RecyclerView adapter to add/remove rows
    CheckinCafesNearbyDialogRecyclerAdapter adapter;

    public CheckinCafesNearbyDialog(Activity a, ArrayList<Business> nearbyCafes) {
        super(a);
        // TODO Auto-generated constructor stub
        this.c = a;
        this.nearbyCafes = nearbyCafes;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_checkin_nearest_cafes);


        // set the custom dialog components - text, image and button
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        adapter = new CheckinCafesNearbyDialogRecyclerAdapter(c.getApplicationContext(), nearbyCafes);
        recyclerView.setLayoutManager(new LinearLayoutManager(c.getApplicationContext()));
        recyclerView.setAdapter(adapter);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
        }
        dismiss();
    }
}

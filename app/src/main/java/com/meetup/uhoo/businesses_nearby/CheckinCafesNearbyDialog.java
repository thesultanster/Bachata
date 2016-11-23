package com.meetup.uhoo.businesses_nearby;

/**
 * Created by sultankhan on 11/13/16.
 */

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.vision.text.Text;
import com.meetup.uhoo.Business;
import com.meetup.uhoo.R;

import java.util.ArrayList;

class CheckinCafesNearbyDialog extends Dialog implements
        android.view.View.OnClickListener{

    public Activity c;
    public Dialog d;
    ArrayList<Business> nearbyCafes;
    // List View to show nearby users
    RecyclerView recyclerView;
    // RecyclerView adapter to add/remove rows
    CheckinCafesNearbyDialogRecyclerAdapter adapter;
    // Interface for business row selected
    CheckinCafesNearbyViewHolderClicks rowClickListener;

    private TextView tvCancel;

    public CheckinCafesNearbyDialog(Activity a, ArrayList<Business> nearbyCafes, CheckinCafesNearbyViewHolderClicks rowClickListener) {
        super(a);

        this.c = a;
        this.nearbyCafes = nearbyCafes;
        this.rowClickListener = rowClickListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_checkin_nearest_cafes);

        // set the custom dialog components - text, image and button
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        adapter = new CheckinCafesNearbyDialogRecyclerAdapter(c.getApplicationContext(), nearbyCafes, rowClickListener);
        recyclerView.setLayoutManager(new LinearLayoutManager(c.getApplicationContext()));
        recyclerView.setAdapter(adapter);

        tvCancel = (TextView) findViewById(R.id.tvCancel);
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

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

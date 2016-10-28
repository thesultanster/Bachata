package com.meetup.uhoo.profile;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.ArraySet;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.meetup.uhoo.R;
import com.meetup.uhoo.User;
import com.meetup.uhoo.util.NavigationDrawerFramework;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SimpleProfileInfo extends NavigationDrawerFramework {

    EditText firstNameEditText;
    EditText lastNameEditText;
    EditText oneLinerEditText;
    Button saveProfileInfoButton;

    private DatabaseReference mDatabase;

    SimpleProfileGridViewAdapter gridAdapter;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_profile_info);

        // Firebase
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Adapter
        GridView gridView = (GridView) findViewById(R.id.gvActivityIcons);
        gridAdapter = new SimpleProfileGridViewAdapter(this, R.layout.grid_item_activity_icon, getData());
        gridView.setAdapter(gridAdapter);


        firstNameEditText = (EditText) findViewById(R.id.firstNameEditText);
        lastNameEditText = (EditText) findViewById(R.id.lastNameEditText);
        oneLinerEditText = (EditText) findViewById(R.id.oneLinerEditText);
        saveProfileInfoButton = (Button) findViewById(R.id.saveProfileInfoButton);

        saveProfileInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(FirebaseAuth.getInstance().getCurrentUser() != null) {

                    Map<String, Object> childUpdates = new HashMap<>();
                    childUpdates.put("/firstName/", firstNameEditText.getText().toString());
                    childUpdates.put("/lastName/", lastNameEditText.getText().toString());
                    childUpdates.put("/oneLiner/", oneLinerEditText.getText().toString());
                    childUpdates.put("/activityIconList/", gridAdapter.getSelectedItems());

                    mDatabase.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).updateChildren(childUpdates, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            if (databaseError == null) {

                            }
                        }
                    });

                    /*
                    mDatabase = FirebaseDatabase.getInstance().getReference("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("activityIconList");
                    for( String activityTitle : gridAdapter.getSelectedItems()) {
                        mDatabase.child(activityTitle).setValue(true);
                    }
                    */



                }


                SharedPreferences.Editor editor = getSharedPreferences("currentUser", MODE_PRIVATE).edit();
                editor.putString("firstName", firstNameEditText.getText().toString());
                editor.putString("lastName", lastNameEditText.getText().toString());
                editor.putString("oneLiner",oneLinerEditText.getText().toString());

                Set<String> set = new HashSet<String>();
                set.addAll(gridAdapter.getSelectedItems());
                editor.putStringSet("activityIconList", set);
                editor.apply();


            }
        });


        // Get User Data if it Exists
        SharedPreferences prefs = getSharedPreferences("currentUser", MODE_PRIVATE);
        String oneLiner = prefs.getString("oneLiner", "");
        String firstname = prefs.getString("firstName","");
        String lastname = prefs.getString("lastName", "");

        Set<String> set = prefs.getStringSet("activityIconList", null);
        ArrayList<String> selectedActivites = new ArrayList<String>(set);

        if(set != null) {
            for (String activity : selectedActivites) {
                for (int i = 0; i < gridAdapter.getCount(); i++) {
                    if (gridAdapter.getSelectedName(i).equals(activity)) {
                        gridAdapter.SetSelected(i);
                    }
                }
            }
        }



        // Populate user data
        firstNameEditText.setText(firstname);
        lastNameEditText.setText(lastname);
        oneLinerEditText.setText(oneLiner);





        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                //SimpleProfileActivityItem item = (SimpleProfileActivityItem) parent.getItemAtPosition(position);
                gridAdapter.SetSelected(position);
                gridAdapter.notifyDataSetChanged();
            }
        });


    }

    // Prepare some dummy data for gridview
    private ArrayList<SimpleProfileActivityItem> getData() {
        final ArrayList<SimpleProfileActivityItem> imageItems = new ArrayList<>();
        TypedArray imgs = getResources().obtainTypedArray(R.array.simple_profile_activity_icons);
        for (int i = 0; i < imgs.length(); i++) {
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), imgs.getResourceId(i, -1));
            imageItems.add(new SimpleProfileActivityItem(bitmap, "Image" + i));
        }
        return imageItems;
    }
}

package com.meetup.uhoo.profile;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.meetup.uhoo.R;
import com.meetup.uhoo.User;
import com.meetup.uhoo.util.NavigationDrawerFramework;

public class SimpleProfileInfo extends NavigationDrawerFramework {

    EditText firstNameEditText;
    EditText lastNameEditText;
    EditText oneLinerEditText;
    Button saveProfileInfoButton;

    private DatabaseReference mDatabase;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_profile_info);

        // Firebase
        mDatabase = FirebaseDatabase.getInstance().getReference();

        firstNameEditText = (EditText) findViewById(R.id.firstNameEditText);
        lastNameEditText = (EditText) findViewById(R.id.lastNameEditText);
        oneLinerEditText = (EditText) findViewById(R.id.oneLinerEditText);
        saveProfileInfoButton = (Button) findViewById(R.id.saveProfileInfoButton);

        saveProfileInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(FirebaseAuth.getInstance().getCurrentUser() != null) {
                    User user = new User(firstNameEditText.getText().toString(), lastNameEditText.getText().toString(), oneLinerEditText.getText().toString());
                    mDatabase.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(SimpleProfileInfo.this, "Profile Saved!", Toast.LENGTH_SHORT).show();

                        }
                    });
                }

                //
                SharedPreferences.Editor editor = getSharedPreferences("currentUser", MODE_PRIVATE).edit();
                editor.putString("name_first", firstNameEditText.getText().toString());
                editor.putString("name_last", lastNameEditText.getText().toString());
                editor.putString("oneLiner",oneLinerEditText.getText().toString());
                editor.apply();


            }
        });


        // Get User Data if it Exists
        SharedPreferences prefs = getSharedPreferences("currentUser", MODE_PRIVATE);
        String oneLiner = prefs.getString("oneLiner", "");
        String firstname = prefs.getString("name_first","");
        String lastname = prefs.getString("name_last", "");

        // Populate user data
        firstNameEditText.setText(firstname);
        lastNameEditText.setText(lastname);
        oneLinerEditText.setText(oneLiner);

    }
}

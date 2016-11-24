package com.meetup.uhoo.profile;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.meetup.uhoo.R;
import com.rey.material.widget.Switch;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by sultankhan on 11/23/16.
 */
public class ProfileBasicsFragment extends Fragment{

    private EditText firstNameEditText;
    private EditText lastNameEditText;
    private EditText oneLinerEditText;
    private Button saveProfileInfoButton;
    private Switch swGender;
    private String gender;

    private DatabaseReference mDatabase;
    private Activity activity;

    public ProfileBasicsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDatabase = FirebaseDatabase.getInstance().getReference();

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        Activity activity;

        if (context instanceof Activity){
            activity = (Activity) context;
            this.activity = activity;
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile_basics, container, false);

        firstNameEditText = (EditText) view.findViewById(R.id.firstNameEditText);
        lastNameEditText = (EditText) view.findViewById(R.id.lastNameEditText);
        oneLinerEditText = (EditText) view.findViewById(R.id.oneLinerEditText);
        swGender = (Switch) view.findViewById(R.id.swGender);

        saveProfileInfoButton = (Button) view.findViewById(R.id.saveProfileInfoButton);
        saveProfileInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Save Basic info on database
                if(FirebaseAuth.getInstance().getCurrentUser() != null) {

                    gender = swGender.isChecked() ? "MALE" : "FEMALE";

                    Map<String, Object> childUpdates = new HashMap<>();
                    childUpdates.put("/firstName/", firstNameEditText.getText().toString());
                    childUpdates.put("/lastName/", lastNameEditText.getText().toString());
                    childUpdates.put("/oneLiner/", oneLinerEditText.getText().toString());
                    childUpdates.put("/gender/", gender);

                    mDatabase.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).updateChildren(childUpdates, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            if (databaseError == null) {

                            }
                        }
                    });

                }

                // Save Data Locally
                SharedPreferences.Editor editor = getActivity().getSharedPreferences("currentUser", 0).edit();
                editor.putString("firstName", firstNameEditText.getText().toString());
                editor.putString("lastName", lastNameEditText.getText().toString());
                editor.putString("oneLiner",oneLinerEditText.getText().toString());
                editor.apply();

                // Trigger Interface
                ((ProfileActivity) activity).onBasicDataChanged();


            }
        });

        // Inflate the layout for this fragment
        return view;
    }
}

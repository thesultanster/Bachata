package com.meetup.uhoo.profile;

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
import com.meetup.uhoo.R;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by sultankhan on 11/23/16.
 */
public class BasicInfoFragment extends Fragment{

    private EditText firstNameEditText;
    private EditText lastNameEditText;
    private EditText oneLinerEditText;
    private Button saveProfileInfoButton;

    private DatabaseReference mDatabase;

    public BasicInfoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_basic_profile_info, container, false);

        firstNameEditText = (EditText) view.findViewById(R.id.firstNameEditText);
        lastNameEditText = (EditText) view.findViewById(R.id.lastNameEditText);
        oneLinerEditText = (EditText) view.findViewById(R.id.oneLinerEditText);

        saveProfileInfoButton = (Button) view.findViewById(R.id.saveProfileInfoButton);
        saveProfileInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Save Basic info on database
                if(FirebaseAuth.getInstance().getCurrentUser() != null) {

                    Map<String, Object> childUpdates = new HashMap<>();
                    childUpdates.put("/firstName/", firstNameEditText.getText().toString());
                    childUpdates.put("/lastName/", lastNameEditText.getText().toString());
                    childUpdates.put("/oneLiner/", oneLinerEditText.getText().toString());
                    //childUpdates.put("/activityIconList/", activitiesView.getSelectedItems());

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

                //activitiesView.save();


            }
        });

        // Inflate the layout for this fragment
        return view;
    }
}

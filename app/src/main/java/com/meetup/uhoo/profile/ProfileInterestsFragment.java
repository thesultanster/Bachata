package com.meetup.uhoo.profile;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.meetup.uhoo.views.InterestsView;
import com.meetup.uhoo.R;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by sultankhan on 11/23/16.
 */
public class ProfileInterestsFragment extends Fragment {

    private InterestsView interestsView;
    private Button btnSaveActivities;

    private Activity activity;
    private DatabaseReference mDatabase;

    public ProfileInterestsFragment() {
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
        View view = inflater.inflate(R.layout.fragment_profile_activities, container, false);

        interestsView = (InterestsView) view.findViewById(R.id.av);
        btnSaveActivities = (Button) view.findViewById(R.id.btnSaveActivities);

        btnSaveActivities.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Save Basic info on database
                if(FirebaseAuth.getInstance().getCurrentUser() != null) {

                    Map<String, Object> childUpdates = new HashMap<>();
                    childUpdates.put("/activityIconList/", interestsView.getSelectedItems());

                    mDatabase.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).updateChildren(childUpdates, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            if (databaseError == null) {

                            }
                        }
                    });

                }

                interestsView.save();


                // Trigger Interface
                ((ProfileActivity) activity).onActivitiesDataChanged();


            }
        });

        return view;
    }
}
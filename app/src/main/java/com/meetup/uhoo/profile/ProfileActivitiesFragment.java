package com.meetup.uhoo.profile;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.meetup.uhoo.ActivitiesView;
import com.meetup.uhoo.R;

/**
 * Created by sultankhan on 11/23/16.
 */
public class ProfileActivitiesFragment extends Fragment {

    private ActivitiesView activitiesView;
    private Button btnSaveActivities;

    private Activity activity;

    public ProfileActivitiesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

        activitiesView = (ActivitiesView) view.findViewById(R.id.av);
        btnSaveActivities = (Button) view.findViewById(R.id.btnSaveActivities);

        btnSaveActivities.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Trigger Interface
                ((ProfileActivity) activity).onActivitiesDataChanged();
            }
        });

        return view;
    }
}
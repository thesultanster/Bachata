package com.meetup.uhoo.views;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.meetup.uhoo.Enum;
import com.meetup.uhoo.R;
import com.meetup.uhoo.core.User;
import com.meetup.uhoo.credentials.SignIn;
import com.meetup.uhoo.profile.ProfileActivity;
import com.meetup.uhoo.service_layer.user_services.CurrentUserDataService;
import com.rey.material.widget.Switch;

/**
 * Created by sultankhan on 11/26/16.
 */
public class CheckinProfileDetailsView extends FrameLayout {

    ProfileRowView pvProfileRow;
    TextView tvEditProfile;
    Switch swAvailability;

    CurrentUserDataService currentUserDataService;
    User currentUser;

    public CheckinProfileDetailsView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(context);
    }

    public CheckinProfileDetailsView(Context context, AttributeSet attrs) {
        super(context, attrs);

        // Get Attributes
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.CheckinProfileDetailsView,
                0, 0
        );

        // Save attribute values
        try {

        } finally {
            a.recycle();
        }

        initView(context);
    }

    public CheckinProfileDetailsView(Context context) {
        super(context);
        initView(context);
    }

    private void initView(final Context context) {
        View view = inflate(getContext(), R.layout.custom_view_checkin_profile_details, null);

        currentUserDataService = new CurrentUserDataService(getContext());
        currentUser = currentUserDataService.getLocalUserData();

        pvProfileRow = (ProfileRowView) view.findViewById(R.id.pvProfileRow);

        swAvailability = (Switch) view.findViewById(R.id.swAvailabilitySwitch);
        swAvailability.setOnCheckedChangeListener(new Switch.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(Switch view, boolean checked) {
                // If user is available, set checking state and update database
                if (checked) {
                    setCheckinVisibilityState(Enum.CheckinVisibilityState.AVAILABLE);
                } else {
                    setCheckinVisibilityState(Enum.CheckinVisibilityState.BUSY);
                }
            }
        });

        // Set Default checked state
        swAvailability.setChecked(currentUser.checkinVisibilityState == Enum.CheckinVisibilityState.AVAILABLE.getValue());

        String d;

        addView(view);
    }

    public void setCheckinVisibilityState(Enum.CheckinVisibilityState visibilityState) {


        pvProfileRow.setCheckinVisibilityState(visibilityState);
        currentUser.checkinVisibilityState = visibilityState.getValue();

        // Save changes to database ( and subsequently save it locally )
        currentUserDataService.setCurrentUser(currentUser);
        currentUserDataService.saveUserToDatabase();
    }

    public void saveProfileDataToDatabase() {
        pvProfileRow.SaveProfileDataToDatabase();
    }

    public void saveProfileDataToLocalUser() {
        pvProfileRow.SaveProfileDataToLocalCurrentUser();
    }

    public Enum.CheckinVisibilityState getCheckinVisibilityState() {
        return pvProfileRow.getCheckinVisibilityState();
    }
}

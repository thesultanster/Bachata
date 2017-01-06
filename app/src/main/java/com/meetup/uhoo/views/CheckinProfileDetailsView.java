package com.meetup.uhoo.views;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.meetup.uhoo.core.Enum;
import com.meetup.uhoo.R;
import com.meetup.uhoo.credentials.SignIn;
import com.meetup.uhoo.profile.ProfileActivity;

/**
 * Created by sultankhan on 11/26/16.
 */
public class CheckinProfileDetailsView extends FrameLayout {

    ProfileRowView pvProfileRow;
    TextView tvEditProfile;

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
        pvProfileRow = (ProfileRowView) view.findViewById(R.id.pvProfileRow);
        tvEditProfile = (TextView) view.findViewById(R.id.tvEditProfile);
        tvEditProfile.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent;

                SharedPreferences prefs = context.getApplicationContext().getSharedPreferences("currentUser", Context.MODE_PRIVATE);
                String authType = prefs.getString("authType", "ANON");

                // If user is ANON then make them sign up
                if(authType.equals("ANON")){
                    intent = new Intent(context.getApplicationContext(), SignIn.class);
                } else {
                    intent = new Intent(context.getApplicationContext(), ProfileActivity.class);
                }


                context.startActivity(intent);
            }
        });
        addView(view);
    }

    public void setCheckinVisibilityState(Enum.CheckinVisibilityState visibilityState){
        pvProfileRow.setCheckinVisibilityState(visibilityState);
    }

    public void saveProfileDataToDatabase(){
        pvProfileRow.SaveProfileDataToDatabase();
    }

    public void saveProfileDataToLocalUser(){
        pvProfileRow.SaveProfileDataToLocalCurrentUser();
    }

    public Enum.CheckinVisibilityState getCheckinVisibilityState(){
        return pvProfileRow.getCheckinVisibilityState();
    }
}

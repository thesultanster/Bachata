package com.meetup.uhoo;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

/**
 * Created by sultankhan on 11/26/16.
 */
public class CheckinProfileDetailsView extends FrameLayout {

    ProfileRowView pvProfileRow;

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

    private void initView(Context context) {
        View view = inflate(getContext(), R.layout.custom_view_checkin_profile_details, null);
        pvProfileRow = (ProfileRowView) view.findViewById(R.id.pvProfileRow);
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

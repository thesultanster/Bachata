package com.meetup.uhoo;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;


/**
 * Created by sultankhan on 11/22/16.
 */
public class ProfileRow extends FrameLayout {

    private String firstName;
    private String lastName;
    private String oneLiner;
    private int visibilityPermission;
    private int type;

    private Context context;
    private SharedPreferences sharedPrefs;

    private TextView tvFullName;
    private TextView tvOneLiner;


    public ProfileRow(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(context);
    }

    public ProfileRow(Context context, AttributeSet attrs) {
        super(context, attrs);

        // Get Attributes
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.ProfileRow,
                0, 0
        );

        // Save attribute values
        try {
            firstName = a.getString(R.styleable.ProfileRow_userFirstName);
            lastName = a.getString(R.styleable.ProfileRow_userLastName);
            visibilityPermission = a.getInt(R.styleable.ProfileRow_visibilityPermission,2);
            type = a.getInt(R.styleable.ProfileRow_type,1);
        } finally {
            a.recycle();
        }

        initView(context);
    }

    public ProfileRow(Context context) {
        super(context);
        initView(context);
    }

    private void initView(Context context) {
        View view = inflate(getContext(), R.layout.custom_view_profile_row, null);

        this.context = context;
        sharedPrefs = context.getSharedPreferences("name", 0);

        tvFullName = (TextView) view.findViewById(R.id.tvFullName);
        tvOneLiner = (TextView) view.findViewById(R.id.miniBio);

        // If the type is Self, then load current user data
        if(type == 1) {
            RefreshCurrentUserData();
        }

        addView(view);
    }


    public void RefreshCurrentUserData(){

        // Get User Data if it Exists
        sharedPrefs = context.getSharedPreferences("currentUser", 0);
        oneLiner = sharedPrefs.getString("oneLiner", "");
        firstName = sharedPrefs.getString("firstName","");
        lastName = sharedPrefs.getString("lastName", "");

        // Populate user data
        tvFullName.setText(firstName + " " + lastName);
        tvOneLiner.setText(oneLiner);
    }


}

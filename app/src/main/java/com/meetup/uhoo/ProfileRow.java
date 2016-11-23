package com.meetup.uhoo;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;


/**
 * Created by sultankhan on 11/22/16.
 */
public class ProfileRow extends FrameLayout {

    private String firstName;
    private String lastName;
    private int visibilityPermission;

    private TextView tvFullName;


    public ProfileRow(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView();
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
            //visibilityPermission = a.(R.styleable.ProfileRow_visibilityPermission,2);
        } finally {
            a.recycle();
        }

        initView();
    }

    public ProfileRow(Context context) {
        super(context);
        initView();
    }

    private void initView() {
        View view = inflate(getContext(), R.layout.custom_view_profile_row, null);

        tvFullName = (TextView) view.findViewById(R.id.tvFullName);
        tvFullName.setText(firstName + " " + lastName);

        addView(view);
    }


}

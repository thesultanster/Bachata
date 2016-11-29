package com.meetup.uhoo;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by sultankhan on 11/22/16.
 */
public class ProfileRowView extends FrameLayout {

    private String firstName;
    private String lastName;
    private String oneLiner;
    private String userId;
    private int visibilityPermission;
    private int type;
    private Enum.CheckinVisibilityState checkinVisibilityState;

    private Context context;
    private SharedPreferences sharedPrefs;
    private DatabaseReference mDatabase;

    private TextView tvFullName;
    private TextView tvOneLiner;
    private TextView tvCheckinState;
    private ImageView ivCheckingState;


    public ProfileRowView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(context);
    }

    public ProfileRowView(Context context, AttributeSet attrs) {
        super(context, attrs);

        // Get Attributes
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.ProfileRowView,
                0, 0
        );

        // Save attribute values
        try {
            firstName = a.getString(R.styleable.ProfileRowView_userFirstName);
            lastName = a.getString(R.styleable.ProfileRowView_userLastName);
            visibilityPermission = a.getInt(R.styleable.ProfileRowView_visibilityPermission,2);
            type = a.getInt(R.styleable.ProfileRowView_type,1);

            if(type == 1){
                userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            }
        } finally {
            a.recycle();
        }

        initView(context);
    }

    public ProfileRowView(Context context) {
        super(context);
        initView(context);
    }

    private void initView(Context context) {
        View view = inflate(getContext(), R.layout.custom_view_profile_row, null);

        this.context = context;
        sharedPrefs = context.getSharedPreferences("name", 0);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        tvFullName = (TextView) view.findViewById(R.id.tvFullName);
        tvOneLiner = (TextView) view.findViewById(R.id.miniBio);
        tvCheckinState = (TextView) view.findViewById(R.id.tvCheckinState);
        ivCheckingState = (ImageView) view.findViewById(R.id.ivCheckinState);

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
        setCheckinVisibilityState (Enum.CheckinVisibilityState.values()[ sharedPrefs.getInt("checkinVisibilityState",0)]);


        // Populate user data
        tvFullName.setText(firstName + " " + lastName);
        tvOneLiner.setText(oneLiner);
    }



    //
    public void SaveProfileDataToDatabase(){

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/firstName/", firstName);
        childUpdates.put("/lastName/", lastName);
        childUpdates.put("/oneLiner/", oneLiner);
        childUpdates.put("/checkinVisibilityState/", checkinVisibilityState.ordinal());

        mDatabase.child("users").child(userId).updateChildren(childUpdates, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError == null) {

                }
            }
        });



    }

    public void SaveProfileDataToLocalCurrentUser(){
        // Save Data Locally
        SharedPreferences.Editor editor = context.getSharedPreferences("currentUser", 0).edit();
        editor.putString("firstName", firstName);
        editor.putString("lastName", lastName);
        editor.putString("oneLiner",oneLiner);
        editor.putInt("checkinVisibilityState", checkinVisibilityState.getValue());
        editor.apply();
    }


    /* Setter Functions
     * Sets different values and updates the view objects
     **********************************************************************************************/
    public void setUserId(String userId){
        this.userId = userId;
    }

    public void setFirstName(String firstName){
        this.firstName = firstName;
        this.tvFullName.setText(firstName + " " + lastName);
    }

    public void setLastName(String lastName){
        this.lastName = lastName;
        this.tvFullName.setText(firstName + " " + lastName);
    }

    public void setOneLiner(String oneLiner){
        this.oneLiner = oneLiner;
        this.tvOneLiner.setText(oneLiner);
    }

    public void setCheckinVisibilityState(Enum.CheckinVisibilityState checkinVisibilityState){
        this.checkinVisibilityState = checkinVisibilityState;

        switch (checkinVisibilityState){
            case AVAILABLE:
                ivCheckingState.setImageResource(R.drawable.fab_checkin_status_available);
                tvCheckinState.setText("Available");
                break;
            case CHECK:
                ivCheckingState.setImageResource(R.drawable.fab_checkin_status_check);
                tvCheckinState.setText("Check");
                break;
            case BUSY:
                ivCheckingState.setImageResource(R.drawable.fab_checkin_status_busy);
                tvCheckinState.setText("Busy");
                break;
            default:
                break;
        }

    }

    public void setData(User user){
        setFirstName( user.getFirstName());
        setLastName( user.getLastName());
        setOneLiner( user.getOneLiner());
        setCheckinVisibilityState( user.getCheckinVisibilityState());
    }


    public Enum.CheckinVisibilityState getCheckinVisibilityState(){
        return checkinVisibilityState;
    }



}

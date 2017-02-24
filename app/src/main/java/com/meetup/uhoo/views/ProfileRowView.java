package com.meetup.uhoo.views;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.meetup.uhoo.Enum;
import com.meetup.uhoo.R;
import com.meetup.uhoo.core.User;
import com.meetup.uhoo.profile.ProfileActivity;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * Created by sultankhan on 11/22/16.
 */
public class ProfileRowView extends FrameLayout {

    private String firstName;
    private String lastName;
    private String oneLiner;
    private String userId;
    private String profileUrl;
    private int visibilityPermission;
    private int type;
    private Enum.CheckinVisibilityState checkinVisibilityState;

    private Context context;
    private SharedPreferences sharedPrefs;
    private DatabaseReference mDatabase;

    private TextView tvOneLiner;
    private TextView tvCheckinState;
    private ImageView ivCheckingState;
    private CircleImageView profileImage;


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

        tvOneLiner = (TextView) view.findViewById(R.id.miniBio);
        tvCheckinState = (TextView) view.findViewById(R.id.tvCheckinState);
        ivCheckingState = (ImageView) view.findViewById(R.id.ivCheckinState);
        profileImage = (CircleImageView) view.findViewById(R.id.profileImage);




        // If the type is Self, then load current user data and set listeners
        if(type == 1) {
            RefreshCurrentUserData();

            view.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getContext(), ProfileActivity.class);
                    getContext().startActivity(intent);
                }
            });

            profileImage.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getContext(), ProfileActivity.class);
                    getContext().startActivity(intent);
                }
            });

            tvOneLiner.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getContext(), ProfileActivity.class);
                    getContext().startActivity(intent);
                }
            });
        }





        addView(view);
    }


    public void RefreshCurrentUserData(){

        // Get User Data if it Exists
        sharedPrefs = context.getSharedPreferences("currentUser", 0);
        oneLiner = sharedPrefs.getString("oneLiner", "");
        firstName = sharedPrefs.getString("firstName","");
        lastName = sharedPrefs.getString("lastName", "");
        profileUrl = sharedPrefs.getString("photoUrl","");

        Log.d("photoUrl", profileUrl);

        setCheckinVisibilityState (Enum.CheckinVisibilityState.values()[ sharedPrefs.getInt("checkinVisibilityState",0)]);

        final Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            public void run() {

                URL img_value = null;
                Bitmap mIcon1 =  null;

                try {
                    img_value = new URL(profileUrl);
                    mIcon1 = BitmapFactory.decodeStream(img_value.openConnection().getInputStream());

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                final Bitmap finalMIcon = mIcon1;
                handler.post(new Runnable(){
                    public void run() {
                        profileImage.setImageBitmap(finalMIcon);
                    }
                });

            }
        };
        new Thread(runnable).start();

        // Populate user data
        tvOneLiner.setText(oneLiner);
    }




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


    public void setOneLiner(String oneLiner){
        this.oneLiner = oneLiner;
        this.tvOneLiner.setText(oneLiner);
    }

    public void setProfileUrl(final String profileUrl){
        this.profileUrl = profileUrl;

        final Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            public void run() {

                URL img_value = null;
                Bitmap mIcon1 =  null;

                try {
                    Log.i("profileUrl", profileUrl);
                    img_value = new URL(profileUrl);
                    mIcon1 = BitmapFactory.decodeStream(img_value.openConnection().getInputStream());
                    final Bitmap finalMIcon = mIcon1;
                    handler.post(new Runnable(){
                        public void run() {
                            profileImage.setImageBitmap(finalMIcon);
                        }
                    });

                } catch (MalformedURLException e) {
                    Log.e("MalformedURLException", profileUrl);
                    e.printStackTrace();
                } catch (IOException e) {
                    Log.e("IOException", profileUrl);
                    e.printStackTrace();
                }



            }
        };
        new Thread(runnable).start();
    }

    public void setCheckinVisibilityState(Enum.CheckinVisibilityState checkinVisibilityState){
        this.checkinVisibilityState = checkinVisibilityState;

        switch (checkinVisibilityState){
            case AVAILABLE:
                ivCheckingState.setImageResource(R.drawable.fab_checkin_status_available);
                tvCheckinState.setText("Approach Me");
                break;
            /*
            case CHECK:
                ivCheckingState.setImageResource(R.drawable.fab_checkin_status_check);
                tvCheckinState.setText("Check");
                break;
                */
            case BUSY:
                ivCheckingState.setImageResource(R.drawable.fab_checkin_status_busy);
                tvCheckinState.setText("Busy");
                break;
            default:
                break;
        }

    }

    public void setData(User user){
        setOneLiner( user.getOneLiner());
        setCheckinVisibilityState(Enum.CheckinVisibilityState.values()[ user.getCheckinVisibilityState()]);
        setProfileUrl(user.getPhotoUrl());
    }


    public Enum.CheckinVisibilityState getCheckinVisibilityState(){
        return checkinVisibilityState;
    }


    public Drawable getProfileDrawable(){
        return profileImage.getBackground();
    }


}

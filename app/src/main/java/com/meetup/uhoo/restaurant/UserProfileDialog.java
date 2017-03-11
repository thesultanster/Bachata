package com.meetup.uhoo.restaurant;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.meetup.uhoo.R;
import com.meetup.uhoo.core.User;
import com.meetup.uhoo.profile.ProfileActivity;
import com.meetup.uhoo.service_layer.user_services.CurrentUserDataService;
import com.meetup.uhoo.views.InterestsView;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by sultankhan on 9/25/16.
 */
public class UserProfileDialog extends Dialog implements
        android.view.View.OnClickListener {

    Button connectButton;
    User user;
    TextView name, miniBio;
    InterestsView ivUserInterests;
    ImageView civProfilePicture;
    ImageView ivEditProfile;

    Activity a;


    public UserProfileDialog(Activity a, User user) {
        super(a);
        this.user = user;
        this.a = a;

        Log.d("UserProfileDialog",  a.getLocalClassName());

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_user_profile);

        connectButton = (Button) findViewById(R.id.connectButton);
        name = (TextView) findViewById(R.id.nameText);
        miniBio = (TextView) findViewById(R.id.miniBio);
        ivUserInterests = (InterestsView) findViewById(R.id.ivUserInterests);
        civProfilePicture = (ImageView) findViewById(R.id.civProfilePicture);
        ivEditProfile = (ImageView) findViewById(R.id.ivEditProfile);

        ivUserInterests.setSelectedItems(user);

        name.setText(user.getFirstName());
        miniBio.setText(user.getOneLiner());

        connectButton.setOnClickListener(this);


        final Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            public void run() {

                URL img_value = null;
                Bitmap mIcon1 =  null;

                try {
                    Log.i("profileUrl", user.getPhotoUrl());
                    img_value = new URL(user.getPhotoUrl());
                    mIcon1 = BitmapFactory.decodeStream(img_value.openConnection().getInputStream());
                    final Bitmap finalMIcon = mIcon1;
                    handler.post(new Runnable(){
                        public void run() {
                            civProfilePicture.setImageBitmap(finalMIcon);
                        }
                    });

                } catch (MalformedURLException e) {
                    Log.e("MalformedURLException", user.getPhotoUrl());
                    e.printStackTrace();
                } catch (IOException e) {
                    Log.e("IOException", user.getPhotoUrl());
                    e.printStackTrace();
                }



            }
        };
        new Thread(runnable).start();

        // Enable edit profile icon if current user is viewing themselves
        CurrentUserDataService currentUserDataService = new CurrentUserDataService(getContext());
        if( user.getUid() != null && user.getUid().equals( currentUserDataService.getCurrentUser().getUid())){


            ivEditProfile.setVisibility(View.VISIBLE);
            ivEditProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent intent = new Intent(getContext(), ProfileActivity.class);
                    getContext().startActivity(intent);
                }
            });
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.connectButton:
                dismiss();
                break;
            default:
                break;
        }
        dismiss();
    }
}

package com.meetup.uhoo.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.meetup.uhoo.R;
import com.meetup.uhoo.util.PermissionsActivity;

public class SplashScreenActivity extends AppCompatActivity {


    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);


    }

    @Override
    protected void onResume() {
        super.onResume();


        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d("token", "Refreshed token: " + refreshedToken);

        // Check If location permission is granted
        if (ContextCompat.checkSelfPermission(SplashScreenActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Go to Permissions Activity
            Intent intent = new Intent(SplashScreenActivity.this, PermissionsActivity.class);
            startActivity(intent);
        } else {



            Intent intent = new Intent(SplashScreenActivity.this, FindLocation.class);
            startActivity(intent);

        }

    }
}

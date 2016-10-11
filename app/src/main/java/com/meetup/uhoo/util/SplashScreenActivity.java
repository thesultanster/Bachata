package com.meetup.uhoo.util;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.meetup.uhoo.R;

public class SplashScreenActivity extends AppCompatActivity {


    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);


        //Intent intent = new Intent(SplashScreenActivity.this, FindLocation.class);
        //startActivity(intent);


    }

    @Override
    protected void onResume() {
        super.onResume();

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

package com.meetup.uhoo.util;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.meetup.uhoo.R;

public class PermissionsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permissions);

        TextView txtNext = (TextView) findViewById(R.id.txtNext);
        txtNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ActivityCompat.requestPermissions(PermissionsActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                        },
                        1);
            }
        });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Intent intent = new Intent(PermissionsActivity.this, SplashScreenActivity.class);
                    startActivity(intent);
                    finish();
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {


                    // Tell the User the location is a requirement
                    Snackbar.make(findViewById(R.id.txtNext), "Location Permissions Are Required", Snackbar.LENGTH_INDEFINITE)
                            .setAction("OKAY FINE", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                    ActivityCompat.requestPermissions(PermissionsActivity.this,
                                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                                            },
                                            1);
                                }
                            })
                            .setActionTextColor(getResources().getColor(android.R.color.holo_blue_light ))
                            .show();

                }

            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
}

package com.meetup.uhoo.util.location;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.provider.Settings;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.meetup.uhoo.R;
import com.meetup.uhoo.people_nearby.PeopleNearby;

public class FindLocation extends Activity {

    FallbackLocationTracker fallbackLocationTracker;
    Bundle args = new Bundle();

    private FirebaseAuth mAuth;

    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_location);

        // Start Location Tracker
        fallbackLocationTracker = new FallbackLocationTracker(this);


        //get the shared instance of the FirebaseAuth object
        mAuth = FirebaseAuth.getInstance();






    }


    @Override
    protected void onStart() {
        super.onStart();


        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(FindLocation.this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {


            ActivityCompat.requestPermissions(FindLocation.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);


        } else {

            FindLocation();

        }

    }




    @Override
    protected void onStop() {
        super.onStop();

        fallbackLocationTracker.stop();
    }

    // Displaying prompt to turn on GPS
    public static void displayPromptForEnablingGPS(final Activity activity) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        final String action = Settings.ACTION_LOCATION_SOURCE_SETTINGS;
        final String message = "Please enable your GPS";

        builder.setMessage(message)
                .setPositiveButton("Go To Settings",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface d, int id) {
                                activity.startActivity(new Intent(action));
                                d.dismiss();
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface d, int id) {
                                d.cancel();
                            }
                        });
        builder.create().show();
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    FindLocation();


                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.


                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }


    private void LoginAnonymousUser(final Location loc) {
        mAuth.signInAnonymously()
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d("firebase auth", "signInAnonymously:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w("firebase auth", "signInAnonymously", task.getException());
                            Toast.makeText(FindLocation.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                        SaveLocationInDatabase(loc);
                    }
                });
    }

    private void FindLocation() {
        // If GPS is turned off, prompt user to turn it on
        if (fallbackLocationTracker != null && !fallbackLocationTracker.isGPSTurnedOn()) {
            displayPromptForEnablingGPS(this);
        }

        if (fallbackLocationTracker != null) {

            // If has fine last known location
            if (fallbackLocationTracker.hasLocation()) {
                LoginAnonymousUser(fallbackLocationTracker.getLocation());
                return;
            }

            // If has course last known location
            if (fallbackLocationTracker.hasPossiblyStaleLocation()) {
                LoginAnonymousUser(fallbackLocationTracker.getPossiblyStaleLocation());
            }

            // Then find fine location
            fallbackLocationTracker.start(new LocationTracker.LocationUpdateListener() {
                @Override
                public void onUpdate(Location oldLoc, long oldTime, Location newLoc, long newTime) {
                    fallbackLocationTracker.stop();
                    LoginAnonymousUser(newLoc);
                }
            });

        }
    }

    private void TerminateSplashScreen(final Location loc) {

        // Save User Location
        args.putParcelable("userPosition", loc);

        // Pass User Location through and go to next activity
        Intent intent = new Intent(FindLocation.this, PeopleNearby.class);
        intent.putExtra("bundle", args);
        startActivity(intent);
        finish();

    }

    private void SaveLocationInDatabase(final Location loc){

        //TODO:: Change Picture and Text to "Currently logging in"

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("locations");
            GeoFire geoFire = new GeoFire(ref);
            geoFire.setLocation(user.getUid(), new GeoLocation(loc.getLatitude(), loc.getLongitude()), new GeoFire.CompletionListener() {
                @Override
                public void onComplete(String key, DatabaseError error) {
                    if (error != null) {
                        Log.d("firebase auth", "There was an error saving the location to GeoFire: " + error);
                    } else {

                        TerminateSplashScreen(loc);

                        Log.d("firebase auth", "Location saved on server successfully!");
                    }
                }
            });
        } else {
            // No user is signed in
        }

    }

}

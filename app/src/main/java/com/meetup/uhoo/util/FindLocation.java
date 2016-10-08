package com.meetup.uhoo.util;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.meetup.uhoo.businesses_nearby.RestaurantsNearby;
import com.meetup.uhoo.util.location.FallbackLocationTracker;
import com.meetup.uhoo.util.location.LocationTracker;

import java.util.HashMap;
import java.util.Map;

public class FindLocation extends Activity {

    FallbackLocationTracker fallbackLocationTracker;
    Bundle args = new Bundle();

    private FirebaseAuth mAuth;
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_location);
        //get the shared instance of the FirebaseAuth object
        mAuth = FirebaseAuth.getInstance();

        // Start Location Tracker
        fallbackLocationTracker = new FallbackLocationTracker(this);

    }


    @Override
    protected void onStart() {
        super.onStart();


        // Check If location permission is granted
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

                    // permission denied, boo!


                }
                return;
            }
        }
    }


    private void LoginAnonymousUser(final Location loc) {

        // Get User shared prefs
        SharedPreferences.Editor editor = getSharedPreferences("currentUser", MODE_PRIVATE).edit();

        // If user is currently signed in
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {

            // cache user logged in state
            editor.putBoolean("isLoggedIn", true);
            editor.apply();

            // Save location in database
            SaveLocationInDatabase(loc);
        }
        // User not signed in
        else {
            editor.putBoolean("isLoggedIn", false);
            editor.apply();
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

                            // Get user shared prefs and save account type as ANONYMOUS
                            SharedPreferences.Editor editor = getSharedPreferences("currentUser", MODE_PRIVATE).edit();
                            editor.putString("authType", "ANON");
                            editor.apply();

                            // Save location in database
                            SaveLocationInDatabase(loc);
                        }
                    });
        }
    }

    private void FindLocation() {
        // If GPS is turned off, prompt user to turn it on
        if (fallbackLocationTracker != null && !fallbackLocationTracker.isGPSTurnedOn()) {
            displayPromptForEnablingGPS(this);
        }

        if (fallbackLocationTracker != null) {


            // If has fine last known location
            //if (fallbackLocationTracker.hasLocation()) {
            //    LoginAnonymousUser(fallbackLocationTracker.getLocation());
            //    return;
            //}

            // TODO: Put this in a timer
            // If has course last known location
            //if (fallbackLocationTracker.hasPossiblyStaleLocation()) {
            //    LoginAnonymousUser(fallbackLocationTracker.getPossiblyStaleLocation());
            //}


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
        Intent intent = new Intent(FindLocation.this, RestaurantsNearby.class);
        intent.putExtra("bundle", args);
        startActivity(intent);
        finish();

    }

    private void SaveLocationInDatabase(final Location loc) {

        // Update user in user_locations GeoFire Table
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("user_locations");
        GeoFire geoFire = new GeoFire(ref);
        geoFire.setLocation(FirebaseAuth.getInstance().getCurrentUser().getUid(), new GeoLocation(loc.getLatitude(), loc.getLongitude()), new GeoFire.CompletionListener() {
            @Override
            public void onComplete(String key, DatabaseError error) {
                if (error != null) {
                    Log.d("firebase auth", "There was an error saving the location to GeoFire: " + error);
                } else {


                    DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

                    Map<String, Object> childUpdates = new HashMap<>();
                    childUpdates.put("/longitude/", loc.getLongitude());
                    childUpdates.put("/latitude/", loc.getLatitude());

                    userRef.updateChildren(childUpdates, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            if (databaseError == null) {
                                Log.d("firebase loc", "Location saved on database");

                                TerminateSplashScreen(loc);
                            }
                        }
                    });



                    Log.d("firebase auth", "Location saved on server successfully!");
                }
            }
        });



    }

}

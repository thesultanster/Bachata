package com.meetup.uhoo.activities;

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
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationSet;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
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
import com.meetup.uhoo.service_layer.auto_checkin_services.AutoCheckinService;
import com.meetup.uhoo.util.location.FallbackLocationTracker;

import java.util.HashMap;
import java.util.Map;

public class FindLocation extends Activity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    FallbackLocationTracker fallbackLocationTracker;
    Bundle args = new Bundle();

    private FirebaseAuth mAuth;
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 0;

    private Button btnPickLocation;
    int PLACE_PICKER_REQUEST = 1;


    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    Double longitude;
    Double latitude;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_location);

        //get the shared instance of the FirebaseAuth object
        mAuth = FirebaseAuth.getInstance();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        // Connect the client.
        mGoogleApiClient.connect();

        LinearLayout llFindLocationButton = (LinearLayout) findViewById(R.id.llFindLocationButton);
        //llFindLocationButton.setVisibility(View.GONE);

        AnimationSet animation = new AnimationSet(true);
        animation.addAnimation(new AlphaAnimation(0.0F, 1.0F));
        animation.setDuration(400);
        animation.setStartOffset(3000);
        llFindLocationButton.startAnimation(animation);

        btnPickLocation = (Button) findViewById(R.id.btnPickLocation);
        btnPickLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (fallbackLocationTracker != null)
                    fallbackLocationTracker.stop();

                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                try {
                    startActivityForResult(builder.build(FindLocation.this), PLACE_PICKER_REQUEST);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
            }
        });

        // Start Location Tracker
        fallbackLocationTracker = new FallbackLocationTracker(this);

    }


    @Override
    protected void onStart() {
        super.onStart();
        FindLocation();
    }


    @Override
    protected void onStop() {
        super.onStop();

        //fallbackLocationTracker.stop();
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(data, this);

                Log.d("Find Location", "onActivityResult:BusinessId:" + place.getId());

                Location location = new Location("");
                location.setLatitude(place.getLatLng().latitude);
                location.setLongitude(place.getLatLng().longitude);

                if (fallbackLocationTracker != null)
                    fallbackLocationTracker.stop();

                LoginAnonymousUser(location);
            }
        }
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


    // TODO: RENAME THIS METHOD
    private void LoginAnonymousUser(final Location loc) {

        // Get User shared prefs
        SharedPreferences.Editor editor = getSharedPreferences("currentUser", MODE_PRIVATE).edit();

        // If user is currently signed in
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {

            // cache user logged in state
            // Get user shared prefs and save account uid
            editor.putString("uid", user.getUid());
            editor.putBoolean("isLoggedIn", true);
            editor.putString("latitude", Double.valueOf(loc.getLatitude()).toString());
            editor.putString("longitude", Double.valueOf(loc.getLongitude()).toString());
            editor.apply();


            // If the user is not anonymous, then start autocheckin service
            SharedPreferences sharedPrefs = getApplicationContext().getSharedPreferences("currentUser", 0);
            String authType = sharedPrefs.getString("authType", "");
            if (!authType.equals("ANON") && !authType.equals("")) {

                // use this to start and trigger a autocheckin
                Intent i = new Intent(getApplicationContext(), AutoCheckinService.class);
                i.putExtra("uid", user.getUid());
                i.putExtra("latitude", loc.getLatitude());
                i.putExtra("longitude", loc.getLongitude());
                //startService(i);

            }


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
                            editor.putBoolean("isLoggedIn", true);
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
    }

    private void TerminateSplashScreen(final Location loc) {

        // Save User Location
        args.putParcelable("userPosition", loc);

        // Pass User Location through and go to next activity
        Intent intent = new Intent(FindLocation.this, IntroScreen.class);
        //TODO: this may be useless
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


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        //mGoogleApiClient.connect();
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(1000); // Update location every second

        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        Log.i("FindLocation", "Location received: " + location.toString());
                        Log.i("FindLocation", "User Location: " + latitude);


                        Location loc2 = new Location("");
                        loc2.setLatitude(location.getLatitude());
                        loc2.setLongitude(location.getLongitude());

                        LoginAnonymousUser(loc2);


                        mGoogleApiClient.disconnect();


                    }
                });
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i("FindLocation", "GoogleApiClient connection has been suspend");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i("FindLocation", "GoogleApiClient connection has failed");
    }
}

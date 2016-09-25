package com.meetup.uhoo.util;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.meetup.uhoo.R;
import com.meetup.uhoo.credentials.SignIn;
import com.meetup.uhoo.people_nearby.RestaurantsNearby;
import com.meetup.uhoo.profile.SimpleProfileInfo;


public class NavigationDrawerFramework extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    //NAVIGATION DRAWER VARIABLES
    //TOOLBAR VARIABLES
    private static final long DRAWER_CLOSE_DELAY_MS = 250;
    private static final String NAV_ITEM_ID = "navItemId";
    private final Handler mDrawerActionHandler = new Handler();
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private int mNavItemId;

    protected FrameLayout mContent;
    protected Toolbar toolbar;

    private FirebaseAuth.AuthStateListener mAuthListener;

    // Cached User Data
    SharedPreferences.Editor editor ;
    SharedPreferences prefs ;


    @Override
    public void setContentView(final int layoutResID) {
        // Your base layout here
        mDrawerLayout = (DrawerLayout) getLayoutInflater().inflate(R.layout.framework_navigation_drawer, null);
        mContent = (FrameLayout) mDrawerLayout.findViewById(R.id.content);

        // Setting the content of layout your provided to the content frame
        getLayoutInflater().inflate(layoutResID, mContent, true);
        super.setContentView(mDrawerLayout);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        // listen for navigation events
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation);
        navigationView.setNavigationItemSelectedListener(this);

        // select the correct nav menu item
        //navigationView.getMenu().findItem(mNavItemId).setChecked(true);

        // set up the hamburger icon to open and close the drawer
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.open, R.string.close);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();

        // navigate(mNavItemId);




    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Cached User Data
        editor = getSharedPreferences("currentUser", MODE_PRIVATE).edit();
        prefs = getSharedPreferences("currentUser", MODE_PRIVATE);

    }

    // Toolbar Code           /===========================================================================
    //====================================================================================================
    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.anonymous_user_toolbar, menu);

        // Get Auth Type and change menu based on that
        String authType = prefs.getString("authType", null);
        if(authType == null)
            return true;


        if (authType != null && authType.equals("EMAIL")) {
            menu.findItem(R.id.create_account_icon).setVisible(false);
            menu.findItem(R.id.create_account_text).setVisible(false);
        }

        Log.d("auth", "NavigationDrawerFramework auth type " + authType);

        return  true;
    }



    // Navigation Drawer Code /===========================================================================
    //====================================================================================================

    @Override
    public boolean onNavigationItemSelected(final MenuItem menuItem) {
        // Update highlighted item in the navigation menu
        menuItem.setChecked(true);
        mNavItemId = menuItem.getItemId();



        // Allow some time after closing the drawer before performing real navigation
        // So the user can see what is happening
        mDrawerLayout.closeDrawer(GravityCompat.START);
        mDrawerActionHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                navigate(menuItem.getItemId());
            }
        }, DRAWER_CLOSE_DELAY_MS);
        return true;
    }

    @Override
    public void onConfigurationChanged(final Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        if (item.getItemId() == android.support.v7.appcompat.R.id.home) {
            return mDrawerToggle.onOptionsItemSelected(item);
        }
        // If user clicks Create Account Icon
        if(item.getItemId() == R.id.create_account_icon){
            Intent intent = new Intent(this, SignIn.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(NAV_ITEM_ID, mNavItemId);
    }





    // Navigation Logic Goes Here
    private void navigate(final int itemId) {

        Intent intent = new Intent();

        switch(itemId){

            case R.id.profile:
                intent = new Intent(this, SimpleProfileInfo.class);
                break;
            case R.id.meet_people:
                intent = new Intent(this, RestaurantsNearby.class);
                break;
            case R.id.logOut:
                // Sign out user from database
                FirebaseAuth.getInstance().signOut();

                // Erase cached authType
                editor.putString("authType", null);
                editor.commit();
                intent = new Intent(this, FindLocation.class);
                break;


        }

        startActivity(intent);

    }

    //====================================================================================================


    public Toolbar getToolbar(){
        return toolbar;
    }



}

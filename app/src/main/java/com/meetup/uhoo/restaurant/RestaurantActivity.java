package com.meetup.uhoo.restaurant;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.meetup.uhoo.AppConstant;
import com.meetup.uhoo.core.User;
import com.meetup.uhoo.views.CheckinProfileDetailsView;
import com.meetup.uhoo.Enum;
import com.meetup.uhoo.R;
import com.meetup.uhoo.core.Business;
import com.meetup.uhoo.core.Survey;
import com.meetup.uhoo.views.SurveyView;
import com.wangjie.androidbucket.utils.ABTextUtil;
import com.wangjie.rapidfloatingactionbutton.RapidFloatingActionButton;
import com.wangjie.rapidfloatingactionbutton.RapidFloatingActionHelper;
import com.wangjie.rapidfloatingactionbutton.RapidFloatingActionLayout;
import com.wangjie.rapidfloatingactionbutton.contentimpl.labellist.RFACLabelItem;
import com.wangjie.rapidfloatingactionbutton.contentimpl.labellist.RapidFloatingActionContentLabelList;

import java.util.ArrayList;
import java.util.List;

public class RestaurantActivity extends AppCompatActivity implements RapidFloatingActionContentLabelList.OnRapidFloatingActionContentLabelListListener{


    // Used to manually update list of nearby users
    SwipeRefreshLayout mSwipeRefreshLayout;

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ViewPagerAdapter adapter;
    private NestedScrollView nsvBottomSheet;

    private FloatingActionButton fabCheckinCheckout;
    private TextView tvCehckinFABLabel;
    private TextView tvCheckinText;

    ValueEventListener postListener;
    DatabaseReference userRef;
    final DatabaseReference ref = FirebaseDatabase.getInstance().getReference("restaurant_locations");
    final GeoFire geoFire = new GeoFire(ref);

    User user;

    private SurveyView svSurveyView;

    private BottomSheetBehavior mBottomSheetBehavior;

    private RapidFloatingActionLayout rfaLayout;
    private RapidFloatingActionButton rfaBtn;
    private RapidFloatingActionHelper rfabHelper;
    private CheckinProfileDetailsView cpdProfileDetailView;

    //String restaurantId;
    Business business;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant);

        // Get business details
        business = (Business) getIntent().getExtras().get("business");
        //restaurantId = getIntent().getExtras().getString("restaurantId");

        InflateVariables();


        // Set Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        try {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle(business.getName());
        } catch (Exception e) {
            Toast.makeText(RestaurantActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
        }

    }

    void InflateVariables() {

        // Shitty way to set only one survey
        // SurveyService surveyService = new SurveyService();
        //surveyService.fetchBusinessSurveys(business.getPlaceId(), new SurveyDataFetchListener() {
        //    @Override
        //    public void onSurveyFetchCompleted(Survey object) {
        //        //svSurveyView.setVisibility(View.VISIBLE);
        //        //svSurveyView.setSurvey(object);
        //    }
        //});

        svSurveyView = (SurveyView) findViewById(R.id.svSurveyView);
        svSurveyView.setBusiness(business.getPlaceId());

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        fabCheckinCheckout = (FloatingActionButton) findViewById(R.id.fbCheckinCheckout);
        tvCehckinFABLabel = (TextView) findViewById(R.id.tvCheckinFABLabel);
        tvCheckinText = (TextView) findViewById(R.id.tvCheckinText);

        cpdProfileDetailView = (CheckinProfileDetailsView) findViewById(R.id.cpdProfileDetailView);
        nsvBottomSheet = (NestedScrollView) findViewById(R.id.nsvBottomSheet);
        mBottomSheetBehavior = BottomSheetBehavior.from(nsvBottomSheet);
        mBottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    rfaLayout.setVisibility(View.GONE);
                }

                if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    rfaLayout.setVisibility(View.GONE);
                }
            }

            @Override
            public void onSlide(View bottomSheet, float slideOffset) {
            }
        });

        rfaLayout = (RapidFloatingActionLayout) findViewById(R.id.fabUserVisibilityMenuLayout);
        rfaBtn = (RapidFloatingActionButton) findViewById(R.id.fabUserVisibilityMenu);
        RapidFloatingActionContentLabelList rfaContent = new RapidFloatingActionContentLabelList(getApplicationContext());
        rfaContent.setOnRapidFloatingActionContentLabelListListener(this);
        List<RFACLabelItem> items = new ArrayList<>();
        items.add(new RFACLabelItem<Integer>()
                .setLabel("Available")
                .setResId(R.mipmap.white_ring)
                .setIconNormalColor(getResources().getColor(R.color.flatDarkGreen))
                .setIconPressedColor(0xffbf360c)
                .setWrapper(0)
        );
        /*
        items.add(new RFACLabelItem<Integer>()
                .setLabel("Check")
                .setResId(R.mipmap.white_ring)
                .setIconNormalColor(getResources().getColor(R.color.flatYellow))
                .setIconPressedColor(0xff1a237e)
                .setWrapper(1)
        );
        */
        items.add(new RFACLabelItem<Integer>()
                .setLabel("Busy")
                .setResId(R.mipmap.white_ring)
                .setIconNormalColor(getResources().getColor(R.color.flatRed))
                .setIconPressedColor(0xff3e2723)
                .setWrapper(1)
        );
        rfaContent
                .setItems(items)
                .setIconShadowRadius(ABTextUtil.dip2px(getApplicationContext(), 5))
                .setIconShadowColor(0xff888888)
                .setIconShadowDy(ABTextUtil.dip2px(getApplicationContext(), 5))
        ;
        rfabHelper = new RapidFloatingActionHelper(
                getApplicationContext(),
                rfaLayout,
                rfaBtn,
                rfaContent
        ).build();
        setRFACItem(cpdProfileDetailView.getCheckinVisibilityState().ordinal());


        fabCheckinCheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Get user auth type. If anon user then tell them to create an account
                SharedPreferences prefs = getSharedPreferences("currentUser", MODE_PRIVATE);
                String authType = prefs.getString("authType", null);
                if (authType != null && authType.equals("ANON")) {
                    Toast.makeText(RestaurantActivity.this, "Please Create An Account", Toast.LENGTH_SHORT).show();
                    return;
                }

                // If the user is not already checked in
                // Check them in
                if (!user.isCheckedIn) {

                    CheckInUser();

                }
                // If the user is already checked in
                // Check them out
                else {
                    CheckOutUser();
                }

                Refresh();

            }
        });



    }


    private void CheckOutUser() {

        // Create database reference
        // We will use this multiple times to update values to check out user
        DatabaseReference mDatabase;

        // Locally save user state as not checked into anything
        SharedPreferences.Editor editor = getSharedPreferences("currentUser", MODE_PRIVATE).edit();
        editor.putString("checkedInto", "");
        editor.putString("checkedIntoBusiness", null);
        editor.apply();

        // Remove user from checkin table on database
        mDatabase = FirebaseDatabase.getInstance().getReference("checkin").child(user.checkedInto);
        mDatabase.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).removeValue();

        // Update user check in state to false on database
        mDatabase = FirebaseDatabase.getInstance().getReference("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        mDatabase.child("isCheckedIn").setValue(false);

    }


    private void CheckInUser() {

        // If user data has not been loaded
        // Don't do anything
        if (user == null) {
            Log.d("user", "data not loaded yet");
            return;
        }

        // Get user auth type. If anon user then tell them to create an account
        SharedPreferences prefs = getSharedPreferences("currentUser", MODE_PRIVATE);
        String authType = prefs.getString("authType", null);
        if (authType != null && authType.equals("ANON")) {
            Toast.makeText(RestaurantActivity.this, "Please Create An Account", Toast.LENGTH_SHORT).show();
            return;
        }


        // If User is not checked in anywhere
        if (!user.isCheckedIn) {

            // Get Selected Place object
            final Business place = business;

            // Add Place Id to GeoFire Table
            // If it exists already, then atleast it updates new LatLng if it is updated
            geoFire.setLocation(place.getPlaceId(), new GeoLocation(place.getLatitude(), place.getLongitude()), new GeoFire.CompletionListener() {
                @Override
                public void onComplete(String key, DatabaseError error) {
                    if (error != null) {
                        System.err.println("There was an error saving the location to GeoFire: " + error);
                    } else {
                        System.out.println("Location ID saved on server successfully!");


                        DatabaseReference mDatabase;

                        // Add user to checkin table
                        mDatabase = FirebaseDatabase.getInstance().getReference("checkin").child(place.getPlaceId());
                        mDatabase.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(true);

                        // Update user isCheckedIn state
                        mDatabase = FirebaseDatabase.getInstance().getReference("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                        mDatabase.child("isCheckedIn").setValue(true);
                        mDatabase.child("checkedInto").setValue(place.getPlaceId());

                        // Update restaurant info on restaurant table
                        mDatabase = FirebaseDatabase.getInstance().getReference("restaurants");
                        mDatabase.child(place.getPlaceId()).setValue(place);


                        // Save placeId of checked in business locally
                        Gson gson = new Gson();
                        String json = gson.toJson(business);
                        SharedPreferences.Editor editor = getSharedPreferences("currentUser", MODE_PRIVATE).edit();
                        editor.putString("checkedInto", place.getPlaceId());
                        editor.putString("checkedIntoBusiness", json);
                        editor.apply();


                        // Create Notifications
                        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext());
                        mBuilder.setSmallIcon(R.mipmap.uhoo_icon);
                        mBuilder.setContentTitle("Checked into " + place.getName());
                        mBuilder.setContentText("You are currently checked into this business");
                        mBuilder.setOngoing(true);
                        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                        // Create Broadcast Intent for Deals button
                        Intent dealsReceive = new Intent();
                        dealsReceive.setAction(AppConstant.DEALS_ACTION);
                        PendingIntent pendingIntentYes = PendingIntent.getBroadcast(getApplicationContext(), 12345, dealsReceive, PendingIntent.FLAG_UPDATE_CURRENT);
                        //mBuilder.addAction(R.mipmap.gps_refresh_icon, "Happenings", pendingIntentYes);

                        // Create Broadcast Intent for Checkout button
                        Intent chekoutReceive = new Intent();
                        chekoutReceive.setAction(AppConstant.CHECKOUT_ACTION);
                        PendingIntent pendingIntentYes2 = PendingIntent.getBroadcast(getApplicationContext(), 12345, chekoutReceive, PendingIntent.FLAG_UPDATE_CURRENT);
                        mBuilder.addAction(R.mipmap.x_grey, "Check Out", pendingIntentYes2);

                        // notificationID allows you to update the notification later on.
                        mNotificationManager.notify(AppConstant.CHECKIN_NOTIF, mBuilder.build());



                    }
                }
            });


        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

    private void setupViewPager(ViewPager viewPager) {
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new PeopleCheckedInFragment(business.getPlaceId()), "People");
        adapter.addFragment(new HappeningsFragment(), "Happenings");
        //adapter.addFragment(new PeopleCheckedInFragment(business.getPlaceId()), "Something Else");
        viewPager.setAdapter(adapter);
    }

    private void startCurrentUserListener() {
        // If one exists, remove it first
        stopCurrentUserListener();

        // Listener for Current User
        postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                user = dataSnapshot.getValue(User.class);
                Log.i("user", "isCheckedIn " + user.isCheckedIn);

                // If user is checked in
                if (user != null && user.isCheckedIn) {
                    tvCheckinText.setText("Checked In");
                    tvCheckinText.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.green_pill));
                    tvCehckinFABLabel.setText("CHECK OUT");
                    fabCheckinCheckout.setImageResource(R.mipmap.check_out);



                } else {
                    tvCheckinText.setText("Not Checked In");
                    tvCheckinText.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.red_pill));
                    tvCehckinFABLabel.setText("CHECK IN");
                    fabCheckinCheckout.setImageResource(R.mipmap.checkin_white);

                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w("user", "loadPost:onCancelled", databaseError.toException());
            }
        };
        userRef = FirebaseDatabase.getInstance().getReference("users/" + FirebaseAuth.getInstance().getCurrentUser().getUid());
        userRef.addValueEventListener(postListener);
    }

    private void stopCurrentUserListener() {
        if (postListener != null) {
            userRef.removeEventListener(postListener);
        }
    }

    private void Refresh(){
        ((PeopleCheckedInFragment)adapter.getItem(0)).Refresh();
    }

    @Override
    protected void onStop() {
        super.onStop();

        stopCurrentUserListener();
    }

    @Override
    public void onStart() {
        super.onStart();

        startCurrentUserListener();
    }

    @Override
    public void onRFACItemLabelClick(int i, RFACLabelItem rfacLabelItem) {
        Toast.makeText(getApplicationContext(), "clicked label: " + i, Toast.LENGTH_SHORT).show();
        rfacItemClick(i);
    }

    @Override
    public void onRFACItemIconClick(int i, RFACLabelItem rfacLabelItem) {
        Toast.makeText(getApplicationContext(), "clicked icon: " + i, Toast.LENGTH_SHORT).show();
        rfacItemClick(i);
    }

    private void rfacItemClick(int i){
        rfabHelper.toggleContent();
        setRFACItem(i);
        cpdProfileDetailView.setCheckinVisibilityState(Enum.CheckinVisibilityState.values()[i]);
        cpdProfileDetailView.saveProfileDataToDatabase();
        cpdProfileDetailView.saveProfileDataToLocalUser();
    }

    private void setRFACItem(int i){

        switch (i){
            case 0:
                rfaBtn.setButtonDrawable(getResources().getDrawable(R.drawable.fab_checkin_status_available) );
                break;
            case 1:
                rfaBtn.setButtonDrawable(getResources().getDrawable(R.drawable.fab_checkin_status_busy) );
                break;
            default:
                break;
        }
        rfaBtn.build();
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}

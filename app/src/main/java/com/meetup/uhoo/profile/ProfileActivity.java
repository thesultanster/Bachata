package com.meetup.uhoo.profile;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.meetup.uhoo.core.User;
import com.meetup.uhoo.core.UserDataFetchListener;
import com.meetup.uhoo.restaurant.RestaurantActivity;
import com.meetup.uhoo.restaurant.UserProfileDialog;
import com.meetup.uhoo.service_layer.user_services.CurrentUserDataService;
import com.meetup.uhoo.service_layer.user_services.UserDataService;
import com.meetup.uhoo.views.ProfileRowView;
import com.meetup.uhoo.R;
import com.meetup.uhoo.util.NavigationDrawerFramework;

import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends AppCompatActivity implements ProfileUpdateInterface{


    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ProfileRowView prCurrentUserProfileRow;
    final Activity activity = this;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Set Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        try {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("Profile");
        } catch (Exception e) {
            Toast.makeText(ProfileActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
        }

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);



        // Setup Profile Row
        prCurrentUserProfileRow = (ProfileRowView) findViewById(R.id.prCurrentUserProfileRow);
        prCurrentUserProfileRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final AlertDialog alertDialog = new AlertDialog.Builder(ProfileActivity.this).create();
                alertDialog.setTitle("Loading...");
                alertDialog.setMessage("Getting Your Profile Data");
                alertDialog.show();

                // Get Current User Object
                User currentUser = (new CurrentUserDataService(getApplicationContext())).getCurrentUser();

                UserDataService userDataService = new UserDataService(currentUser.getUid());
                userDataService.getFirebaseUserData(new UserDataFetchListener() {
                    @Override
                    public void onUserFetch(User user) {



                        // Pass in user object and create Profile Dialog
                        UserProfileDialog userDialog = new UserProfileDialog(activity, user);
                        userDialog.show();
                        Window window = userDialog.getWindow();
                        window.setLayout(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);

                        alertDialog.dismiss();
                    }
                });




            }
        });



    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new ProfileBasicsFragment(), "Basics");
        adapter.addFragment(new ProfilePictureFragment(), "Picture");
        adapter.addFragment(new ProfileInterestsFragment(), "Interests");
        viewPager.setAdapter(adapter);
    }

    @Override
    public void onBasicDataChanged() {
        Log.i("Frag interface","onBasicDataChanged");

        prCurrentUserProfileRow.RefreshCurrentUserData();
        Toast.makeText(ProfileActivity.this, "Basic Info Saved!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onActivitiesDataChanged() {
        Log.i("Frag interface","onActivitiesDataChanged");


        final AlertDialog alertDialog = new AlertDialog.Builder(ProfileActivity.this).create();
        alertDialog.setTitle("Loading...");
        alertDialog.setMessage("Getting Your Profile Data");
        alertDialog.show();

        // Get Current User Object
        User currentUser = (new CurrentUserDataService(getApplicationContext())).getCurrentUser();

        UserDataService userDataService = new UserDataService(currentUser.getUid());
        userDataService.getFirebaseUserData(new UserDataFetchListener() {
            @Override
            public void onUserFetch(User user) {



                // Pass in user object and create Profile Dialog
                UserProfileDialog userDialog = new UserProfileDialog(activity, user);
                userDialog.show();
                Window window = userDialog.getWindow();
                window.setLayout(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);

                alertDialog.dismiss();
            }
        });

        //Toast.makeText(ProfileActivity.this, "Interests Saved!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onProfilePictureDataChanged() {
        Log.i("Frag interface","onProfilePictureDataChanged");




        final AlertDialog alertDialog = new AlertDialog.Builder(ProfileActivity.this).create();
        alertDialog.setTitle("Loading...");
        alertDialog.setMessage("Getting Your Profile Data");
        alertDialog.show();

        // Get Current User Object
        User currentUser = (new CurrentUserDataService(getApplicationContext())).getCurrentUser();

        UserDataService userDataService = new UserDataService(currentUser.getUid());
        userDataService.getFirebaseUserData(new UserDataFetchListener() {
            @Override
            public void onUserFetch(User user) {



                // Pass in user object and create Profile Dialog
                UserProfileDialog userDialog = new UserProfileDialog(activity, user);
                userDialog.show();
                Window window = userDialog.getWindow();
                window.setLayout(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);

                alertDialog.dismiss();
            }
        });

        prCurrentUserProfileRow.RefreshCurrentUserData();
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

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        View v = getCurrentFocus();

        if (v != null &&
                (ev.getAction() == MotionEvent.ACTION_UP || ev.getAction() == MotionEvent.ACTION_MOVE) &&
                v instanceof EditText &&
                !v.getClass().getName().startsWith("android.webkit.")) {
            int scrcoords[] = new int[2];
            v.getLocationOnScreen(scrcoords);
            float x = ev.getRawX() + v.getLeft() - scrcoords[0];
            float y = ev.getRawY() + v.getTop() - scrcoords[1];

            if (x < v.getLeft() || x > v.getRight() || y < v.getTop() || y > v.getBottom())
                hideKeyboard(this);
        }
        return super.dispatchTouchEvent(ev);
    }

    public static void hideKeyboard(Activity activity) {
        if (activity != null && activity.getWindow() != null && activity.getWindow().getDecorView() != null) {
            InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(activity.getWindow().getDecorView().getWindowToken(), 0);
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

}

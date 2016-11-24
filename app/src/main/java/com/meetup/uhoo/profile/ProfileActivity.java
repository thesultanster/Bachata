package com.meetup.uhoo.profile;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.widget.Toast;

import com.meetup.uhoo.ProfileRow;
import com.meetup.uhoo.R;
import com.meetup.uhoo.util.NavigationDrawerFramework;

import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends NavigationDrawerFramework implements ProfileUpdateInterface{


    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ProfileRow prCurrentUserProfileRow;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        prCurrentUserProfileRow = (ProfileRow) findViewById(R.id.prCurrentUserProfileRow);


    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new ProfileBasicsFragment(), "Basics");
        adapter.addFragment(new ProfileActivitiesFragment(), "Activities");
        adapter.addFragment(new ProfileBasicsFragment(), "Another");
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

        Toast.makeText(ProfileActivity.this, "Activities Saved!", Toast.LENGTH_SHORT).show();
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

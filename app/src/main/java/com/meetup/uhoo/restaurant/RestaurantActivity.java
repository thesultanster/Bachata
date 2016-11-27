package com.meetup.uhoo.restaurant;

import android.content.Context;
import android.graphics.Color;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.meetup.uhoo.R;
import com.meetup.uhoo.Business;
import com.meetup.uhoo.User;
import com.wangjie.androidbucket.utils.ABTextUtil;
import com.wangjie.androidbucket.utils.imageprocess.ABShape;
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

    private RapidFloatingActionLayout rfaLayout;
    private RapidFloatingActionButton rfaBtn;
    private RapidFloatingActionHelper rfabHelper;

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
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

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
        items.add(new RFACLabelItem<Integer>()
                .setLabel("Check")
                .setResId(R.mipmap.white_ring)
                .setIconNormalColor(getResources().getColor(R.color.flatYellow))
                .setIconPressedColor(0xff1a237e)
                .setWrapper(1)
        );
        items.add(new RFACLabelItem<Integer>()
                .setLabel("Busy")
                .setResId(R.mipmap.white_ring)
                .setIconNormalColor(getResources().getColor(R.color.flatRed))
                .setIconPressedColor(0xff3e2723)
                .setWrapper(2)
        );
        items.add(new RFACLabelItem<Integer>()
                .setLabel("Anonymous")
                .setResId(R.mipmap.white_ring)
                .setIconNormalColor(getResources().getColor(R.color.flatBlue))
                .setIconPressedColor(0xff0d5302)
                .setWrapper(3)
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
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new PeopleCheckedInFragment(business.getPlaceId()), "People");
        adapter.addFragment(new HappeningsFragment(), "Happenings");
        adapter.addFragment(new PeopleCheckedInFragment(business.getPlaceId()), "Something Else");
        viewPager.setAdapter(adapter);
    }

    @Override
    public void onRFACItemLabelClick(int i, RFACLabelItem rfacLabelItem) {
        Toast.makeText(getApplicationContext(), "clicked label: " + i, Toast.LENGTH_SHORT).show();
        rfabHelper.toggleContent();

        switch (i){
            case 0:
                rfaBtn.setButtonDrawable(getResources().getDrawable(R.drawable.fab_checkin_status_available) );
                break;
            case 1:
                rfaBtn.setButtonDrawable(getResources().getDrawable(R.drawable.fab_checkin_status_check) );
                break;
            case 2:
                rfaBtn.setButtonDrawable(getResources().getDrawable(R.drawable.fab_checkin_status_busy) );
                break;
            case 3:
                rfaBtn.setButtonDrawable(getResources().getDrawable(R.drawable.fab_checkin_status_anonymous) );
                break;
            default:
                break;
        }
        rfaBtn.build();
    }

    @Override
    public void onRFACItemIconClick(int i, RFACLabelItem rfacLabelItem) {
        Toast.makeText(getApplicationContext(), "clicked icon: " + i, Toast.LENGTH_SHORT).show();
        rfabHelper.toggleContent();

        switch (i){
            case 0:
                rfaBtn.setButtonDrawable(getResources().getDrawable(R.drawable.fab_checkin_status_available) );
                break;
            case 1:
                rfaBtn.setButtonDrawable(getResources().getDrawable(R.drawable.fab_checkin_status_check) );
                break;
            case 2:
                rfaBtn.setButtonDrawable(getResources().getDrawable(R.drawable.fab_checkin_status_busy) );
                break;
            case 3:
                rfaBtn.setButtonDrawable(getResources().getDrawable(R.drawable.fab_checkin_status_anonymous) );
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

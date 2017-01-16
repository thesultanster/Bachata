package com.meetup.uhoo.views;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.meetup.uhoo.R;
import com.meetup.uhoo.core.Survey;
import com.meetup.uhoo.restaurant.HappeningsFragment;
import com.meetup.uhoo.restaurant.PeopleCheckedInFragment;
import com.meetup.uhoo.service_layer.SurveyDataFetchListener;
import com.meetup.uhoo.service_layer.SurveyService;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sultankhan on 12/18/16.
 */
public class SurveyView extends FrameLayout {

    private Context context;
    private ViewPagerAdapter adapter;

    ViewPager vpSurveyViewPager;

    public SurveyView(Context context) {
        super(context);
        initView(context);
    }

    public SurveyView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public SurveyView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public SurveyView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView(context);
    }


    private void initView(Context context) {
        View view = inflate(context, R.layout.custom_view_survey, null);
        this.context = context;

        vpSurveyViewPager = (ViewPager) view.findViewById(R.id.vpSurveyViewPager);
        setupViewPager(vpSurveyViewPager);

        TabLayout tabLayout = (TabLayout) view.findViewById(R.id.tabDots);
        tabLayout.setupWithViewPager(vpSurveyViewPager, true);

        addView(view);
    }



    private void setupViewPager(ViewPager viewPager) {
        adapter = new ViewPagerAdapter(((AppCompatActivity) context).getSupportFragmentManager());
        viewPager.setAdapter(adapter);
    }


    public void setBusiness(String businessId){
        Log.i("setBusiness", "businessId: " + businessId);

        final SurveyService surveyService = new SurveyService();
        surveyService.fetchBusinessSurveys(businessId, new SurveyDataFetchListener() {
            @Override
            public void onSurveyFetched(Survey object) {
                Log.i("setBusiness", "onSurveyFetched:surveyTitle: " + object.getTitle());


                adapter.addFragment(SurveyFragment.newInstance(object), "");
            }
        });
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
            notifyDataSetChanged();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }


}

package com.meetup.uhoo;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.GridView;

import com.meetup.uhoo.profile.SimpleProfileActivityItem;
import com.meetup.uhoo.profile.SimpleProfileGridViewAdapter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by sultankhan on 10/28/16.
 */
public class ActivityView extends FrameLayout {

    private GridView gridView;
    private SimpleProfileGridViewAdapter gridAdapter;


    public ActivityView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView();
    }

    public ActivityView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public ActivityView(Context context) {
        super(context);
        initView();
    }

    private void initView() {
        View view = inflate(getContext(), R.layout.custom_view_activity, null);

        // Adapter
        gridView = (GridView) view.findViewById(R.id.gvActivityIcons);
        gridAdapter = new SimpleProfileGridViewAdapter(getContext(), R.layout.grid_item_activity_icon, getData());
        gridView.setAdapter(gridAdapter);


        // Get User Data if it Exists
        SharedPreferences prefs = getContext().getSharedPreferences("currentUser", 0);
        Set<String> set = prefs.getStringSet("activityIconList", null);
        ArrayList<String> selectedActivites = new ArrayList<String>(set);
        if(set != null) {
            for (String activity : selectedActivites) {
                for (int i = 0; i < gridAdapter.getCount(); i++) {
                    if (gridAdapter.getSelectedName(i).equals(activity)) {
                        gridAdapter.SetSelected(i);
                    }
                }
            }
        }

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                //SimpleProfileActivityItem item = (SimpleProfileActivityItem) parent.getItemAtPosition(position);
                gridAdapter.SetSelected(position);
                gridAdapter.notifyDataSetChanged();
            }
        });
        addView(view);
    }

    // Prepare some dummy data for gridview
    private ArrayList<SimpleProfileActivityItem> getData() {
        final ArrayList<SimpleProfileActivityItem> imageItems = new ArrayList<>();
        TypedArray imgs = getResources().obtainTypedArray(R.array.simple_profile_activity_icons);
        for (int i = 0; i < imgs.length(); i++) {
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), imgs.getResourceId(i, -1));
            imageItems.add(new SimpleProfileActivityItem(bitmap, "Image" + i));
        }
        return imageItems;
    }

    public void save(){
        SharedPreferences.Editor editor = getContext().getSharedPreferences("currentUser", 0).edit();
        Set<String> set = new HashSet<String>();
        set.addAll(gridAdapter.getSelectedItems());
        editor.putStringSet("activityIconList", set);
        editor.apply();
    }

    public ArrayList<String> getSelectedItems(){
        return gridAdapter.getSelectedItems();
    }

}

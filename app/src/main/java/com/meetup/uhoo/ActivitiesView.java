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
public class ActivitiesView extends FrameLayout {

    private GridView gridView;
    private SimpleProfileGridViewAdapter gridAdapter;

    private ArrayList<String> selectedActivites;
    private boolean readOnly = false;


    public ActivitiesView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView();
    }

    public ActivitiesView(Context context, AttributeSet attrs) {
        super(context, attrs);

        // Get Attributes
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.ActivitiesView,
                0, 0
        );

        // Save attribute values
        try {
            readOnly = a.getBoolean(R.styleable.ActivitiesView_readOnly, false);
        } finally {
            a.recycle();
        }

        initView();
    }

    public ActivitiesView(Context context) {
        super(context);
        initView();
    }

    private void initView() {
        View view = inflate(getContext(), R.layout.custom_view_activity, null);


        // Get User Data if it Exists
        SharedPreferences prefs = getContext().getSharedPreferences("currentUser", 0);
        Set<String> set = prefs.getStringSet("activityIconList", null);
        selectedActivites = new ArrayList<>();
        if(set!=null)
            selectedActivites = new ArrayList<>(set);


        // Adapter
        gridView = (GridView) view.findViewById(R.id.gvActivityIcons);
        gridAdapter = new SimpleProfileGridViewAdapter(getContext(), R.layout.grid_item_activity_icon, getData());
        gridView.setAdapter(gridAdapter);

        // If not read only then enable selected backgrounds on saved items
        if (!readOnly) {
            if (set != null) {
                for (String activity : selectedActivites) {
                    for (int i = 0; i < gridAdapter.getCount(); i++) {
                        if (gridAdapter.getSelectedName(i).equals(activity)) {
                            gridAdapter.SetSelected(i);
                        }
                    }
                }
            }
        }

        // If readOnly then dont enable activity selecting
        if (!readOnly) {
            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                    //SimpleProfileActivityItem item = (SimpleProfileActivityItem) parent.getItemAtPosition(position);
                    gridAdapter.SetSelected(position);
                    gridAdapter.notifyDataSetChanged();
                }
            });
        }

        gridView.setVerticalScrollBarEnabled(false);

        addView(view);
    }

    // Prepare some dummy data for gridview
    private ArrayList<SimpleProfileActivityItem> getData() {
        final ArrayList<SimpleProfileActivityItem> imageItems = new ArrayList<>();
        TypedArray imgs = getResources().obtainTypedArray(R.array.simple_profile_activity_icons);
        TypedArray names = getResources().obtainTypedArray(R.array.simple_profile_activity_names);
        for (int i = 0; i < imgs.length(); i++) {
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), imgs.getResourceId(i, -1));

            // If not read only then add all items to list
            if (!readOnly) {
                imageItems.add(new SimpleProfileActivityItem(bitmap, names.getString(i)));
            } else {

                // If read only then only show the user's saved activity items
                for (String activity : selectedActivites) {

                    if ((names.getString(i)).equals(activity)) {
                        imageItems.add(new SimpleProfileActivityItem(bitmap, names.getString(i)));
                    }
                }
            }
        }

        imgs.recycle();
        names.recycle();
        return imageItems;
    }

    public void save() {
        SharedPreferences.Editor editor = getContext().getSharedPreferences("currentUser", 0).edit();
        Set<String> set = new HashSet<String>();
        set.addAll(gridAdapter.getSelectedItems());
        editor.putStringSet("activityIconList", set);
        editor.apply();
    }

    public ArrayList<String> getSelectedItems() {
        return gridAdapter.getSelectedItems();
    }

}

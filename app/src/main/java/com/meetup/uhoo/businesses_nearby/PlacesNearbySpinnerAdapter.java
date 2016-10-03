package com.meetup.uhoo.businesses_nearby;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.meetup.uhoo.R;

import java.util.Collections;
import java.util.List;

/**
 * Created by sultankhan on 9/17/16.
 */
public class PlacesNearbySpinnerAdapter extends ArrayAdapter<PlacesNearbySpinnerInfo> {

    // Your sent context
    private Context context;
    List<PlacesNearbySpinnerInfo> data = Collections.emptyList();

    public PlacesNearbySpinnerAdapter(Context context, int textViewResourceId, List<PlacesNearbySpinnerInfo> data) {
        super(context, textViewResourceId);
        this.context = context;
        this.data = data;
    }

    public int getCount() {
        return data.size();
    }

    public PlacesNearbySpinnerInfo getItem(int position) {
        return data.get(position);
    }

    public long getItemId(int position) {
        return position;
    }


    // This is for the "passive" state of the spinner
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


        LayoutInflater inflater = LayoutInflater.from(context);
        View row = inflater.inflate(R.layout.row_places_nearby, parent, false);

        TextView name = (TextView) row.findViewById(R.id.placeNameText);
        name.setTextColor(Color.BLACK);
        name.setText(data.get(position).getName());

        return row;
    }

    // And here is when the "chooser" is popped up
    // Normally is the same view, but you can customize it if you want
    @Override
    public View getDropDownView(int position, View convertView,
                                ViewGroup parent) {


        LayoutInflater inflater = LayoutInflater.from(context);
        View row = inflater.inflate(R.layout.row_places_nearby, parent, false);

        TextView name = (TextView) row.findViewById(R.id.placeNameText);
        name.setTextColor(Color.BLACK);
        name.setText(data.get(position).getName());

        return row;

    }
}


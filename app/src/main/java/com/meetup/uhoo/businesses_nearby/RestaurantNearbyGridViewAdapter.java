package com.meetup.uhoo.businesses_nearby;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.meetup.uhoo.R;
import com.meetup.uhoo.profile.SimpleProfileActivityItem;

import java.util.ArrayList;

/**
 * Created by sultankhan on 10/29/16.
 */


public class RestaurantNearbyGridViewAdapter extends ArrayAdapter {
    private Context context;
    private int layoutResourceId;
    private ArrayList<RestaurantNearbyGridViewItem> data = new ArrayList<RestaurantNearbyGridViewItem>();
    private ArrayList<String> selectedList = new ArrayList<String>();

    public RestaurantNearbyGridViewAdapter(Context context, int layoutResourceId, ArrayList<RestaurantNearbyGridViewItem> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder holder = null;

        if (row == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            row = inflater.inflate(layoutResourceId, parent, false);
            holder = new ViewHolder();
            holder.image = (ImageView) row.findViewById(R.id.ivActivityIcon);
            holder.llGridItem = (LinearLayout) row.findViewById(R.id.llgridItemActivity);
            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }

        RestaurantNearbyGridViewItem item = data.get(position);
        return row;
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    static class ViewHolder {
        ImageView image;
        LinearLayout llGridItem;
    }

    public void addProfile(){
        data.add(new RestaurantNearbyGridViewItem());
        this.notifyDataSetChanged();
    }


}

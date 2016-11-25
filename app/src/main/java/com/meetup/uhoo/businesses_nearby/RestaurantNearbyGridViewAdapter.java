package com.meetup.uhoo.businesses_nearby;

import android.content.Context;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.meetup.uhoo.R;
import com.meetup.uhoo.User;
import com.meetup.uhoo.UserDataFetchListener;
import com.meetup.uhoo.profile.SimpleProfileActivityItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by sultankhan on 10/29/16.
 */


public class RestaurantNearbyGridViewAdapter extends ArrayAdapter {
    private Context context;
    private int layoutResourceId;
    private List<User> usersCheckedIn = Collections.emptyList();

    public RestaurantNearbyGridViewAdapter(Context context, int layoutResourceId, List<User> usersCheckedIn) {
        super(context, layoutResourceId, usersCheckedIn);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.usersCheckedIn = usersCheckedIn;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder holder = null;

        // If brand new view row created
        if (row == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new ViewHolder();
            holder.image = (ImageView) row.findViewById(R.id.ivProfileIcon);
            holder.llGridItem = (LinearLayout) row.findViewById(R.id.llgridItemActivity);


            // Save data, later used for recycling
            row.setTag(holder);
        }
        // If row is recycled
        else {
            // Fetch old data
            holder = (ViewHolder) row.getTag();

        }

        // Set gender of viewholder objectGender LoafLoadasdfasdsadf
        if(usersCheckedIn.size() > 0) {
            switch (usersCheckedIn.get(position).getGender()) {
                case "MALE":
                    holder.image.setColorFilter(ContextCompat.getColor(context, R.color.pastelBlue));
                    break;
                case "FEMALE":
                    holder.image.setColorFilter(ContextCompat.getColor(context, R.color.pastelRed));
                    break;
                default:
                    break;
            }
        }

        return row;
    }

    @Override
    public Object getItem(int position) {
        return usersCheckedIn.get(position);
    }

    static class ViewHolder {
        ImageView image;
        LinearLayout llGridItem;
    }


    public void update(List<User> usersCheckedIn) {
        //this.usersCheckedIn = Collections.emptyList();
        //this.notifyDataSetChanged();
        this.usersCheckedIn = usersCheckedIn;
        this.notifyDataSetChanged();
    }


    public void addProfile(){
        usersCheckedIn.add(new User());
        this.notifyDataSetChanged();
    }


}

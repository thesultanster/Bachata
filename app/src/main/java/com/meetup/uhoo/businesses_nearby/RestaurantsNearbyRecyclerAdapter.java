package com.meetup.uhoo.businesses_nearby;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.TextView;

import com.meetup.uhoo.Business;
import com.meetup.uhoo.R;
import com.meetup.uhoo.User;
import com.meetup.uhoo.profile.SimpleProfileActivityItem;
import com.meetup.uhoo.profile.SimpleProfileGridViewAdapter;
import com.meetup.uhoo.restaurant.RestaurantActivity;
import com.meetup.uhoo.restaurant.UserDataFetchListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class RestaurantsNearbyRecyclerAdapter extends RecyclerView.Adapter<RestaurantsNearbyRecyclerAdapter.MyViewHolder> {

    // emptyList takes care of null pointer exception
    List<Business> data = Collections.emptyList();
    LayoutInflater inflator;
    Context context;

    public RestaurantsNearbyRecyclerAdapter(RestaurantsNearby context, List<Business> data) {
        this.context = context;
        inflator = LayoutInflater.from(context);
        this.data = data;
    }



    public void addRow(Business row) {
        data.add(row);
        notifyItemInserted(getItemCount() - 1);
    }

    public void clearData() {
        int size = this.data.size();

        data.clear();

        this.notifyItemRangeRemoved(0, size);
    }

    // Called when the recycler view needs to create a new row
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        final View view = inflator.inflate(R.layout.row_business_nearby, parent, false);
        MyViewHolder holder = new MyViewHolder(context, view, new MyViewHolder.MyViewHolderClicks() {

            public void rowClick(View caller, int position) {
                Log.d("rowClick", "rowClicks");

                Intent intent = new Intent(context, RestaurantActivity.class);
                //intent.putExtra("restaurantId", data.get(position).placeId);
                intent.putExtra("business", data.get(position));
                view.getContext().startActivity(intent);
            }

            @Override
            public void bookNow(View caller, int position) {

                //Intent intent = new Intent(context, SendSticker.class);
                //intent.putExtra("userId", data.get(position).getParseObjectId());
                //view.getContext().startActivity(intent);
            }


        });
        return holder;
    }

    // Setting up the data for each row
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        // This gives us current information list object
        Business current = data.get(position);

        holder.name.setText(current.getName());
        holder.checkinText.setText(current.getNumUsersCheckedIn() + " users checked in");
        holder.numUsersCheckedInt = current.getNumUsersCheckedIn();

        for(int i = 0; i < holder.numUsersCheckedInt; i++){
            holder.gridAdapter.addProfile();
        }

    }


    @Override
    public int getItemCount() {
        return data.size();
    }

    // Created my custom view holder
    public static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView name, checkinText;
        Context context;
        int numUsersCheckedInt;

        CircleImageView profileImage;
        public MyViewHolderClicks mListener;

        GridView gridView;
        RestaurantNearbyGridViewAdapter gridAdapter;

        // itemView will be my own custom layout View of the row
        public MyViewHolder(Context context, View itemView, MyViewHolderClicks listener) {
            super(itemView);
            this.context = context;
            mListener = listener;

            //Link the objects
            name = (TextView) itemView.findViewById(R.id.name);
            checkinText = (TextView) itemView.findViewById(R.id.checkinText);
            profileImage = (CircleImageView) itemView.findViewById(R.id.profileImage);


            ArrayList<RestaurantNearbyGridViewItem> imageItems = new ArrayList<>();


            // Adapter
            gridView = (GridView) itemView.findViewById(R.id.gvProfileIcons);
            gridAdapter = new RestaurantNearbyGridViewAdapter(context, R.layout.grid_item_profile_icon, imageItems);
            gridView.setAdapter(gridAdapter);


            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {

                default:
                    mListener.rowClick(v, getAdapterPosition());
                    break;
            }
        }

        public interface MyViewHolderClicks {
            void rowClick(View caller, int position);

            void bookNow(View caller, int position);

        }
    }


}
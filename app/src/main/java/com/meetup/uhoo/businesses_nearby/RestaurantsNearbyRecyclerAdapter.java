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

import com.meetup.uhoo.core.Business;
import com.meetup.uhoo.R;
import com.meetup.uhoo.core.User;
import com.meetup.uhoo.core.UserCheckinListener;
import com.meetup.uhoo.restaurant.RestaurantActivity;
import com.meetup.uhoo.service_layer.business_services.BusinessService;

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
        if( exists(row) ){
            return;
        }

        // Get index of new row
        final int index = getItemCount();

        data.add(row);
        notifyItemInserted(index);

        BusinessService businessService = new BusinessService(row);
        businessService.fetchCheckedInUserData(new UserCheckinListener() {
            @Override
            public void onFetchUsersCheckedIn(List<User> checkedInUsers) {
                data.get(index).setUsersCheckedIn(checkedInUsers);
                notifyItemChanged(index);
            }
        });




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
    public void onBindViewHolder(final MyViewHolder holder, int position) {

        // This gives us current business
        Business currentBusiness = data.get(position);

        BusinessService businessService = new BusinessService(currentBusiness);
        businessService.fetchCheckedInUserData(new UserCheckinListener() {
            @Override
            public void onFetchUsersCheckedIn(List<User> checkedInUsers) {
               holder.gridAdapter.update(checkedInUsers);
            }
        });


        List<User> checkedInUsers = currentBusiness.usersCheckedIn;

        holder.name.setText(currentBusiness.getName());
        holder.checkinText.setText(currentBusiness.getNumUsersCheckedIn() + " users checked in");
        holder.numUsersCheckedInt = currentBusiness.getNumUsersCheckedIn();

        holder.gridAdapter = new RestaurantNearbyGridViewAdapter(context, R.layout.grid_item_profile_icon, checkedInUsers);
        holder.gridView.setAdapter(holder.gridAdapter);


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
            gridView = (GridView) itemView.findViewById(R.id.gvProfileIcons);

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


    private boolean exists(Business row){

        for ( Business business : data){
            if(row.getPlaceId().equals(business.getPlaceId())){


                return true;
            }
        }
        return false;

    }


}
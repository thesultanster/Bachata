package com.meetup.uhoo.businesses_nearby;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.meetup.uhoo.Business;
import com.meetup.uhoo.R;

import java.util.Collections;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CheckinCafesNearbyDialogRecyclerAdapter extends RecyclerView.Adapter<CheckinCafesNearbyDialogRecyclerAdapter.MyViewHolder> {

    // emptyList takes care of null pointer exception
    List<Business> data = Collections.emptyList();
    LayoutInflater inflator;
    Context context;
    CheckinCafesNearbyViewHolderClicks rowClickListener;

    public CheckinCafesNearbyDialogRecyclerAdapter(Context context, List<Business> data, CheckinCafesNearbyViewHolderClicks rowClickListener) {
        this.context = context;
        this.inflator = LayoutInflater.from(context);
        this.data = data;
        this.rowClickListener = rowClickListener;
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

        final View view = inflator.inflate(R.layout.row_checkin_nearest_restaurant, parent, false);
        MyViewHolder holder = new MyViewHolder(context, view, rowClickListener);
        return holder;
    }

    // Setting up the data for each row
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        // This gives us current information list object
        Business current = data.get(position);

        holder.name.setText(current.getName());

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
        public CheckinCafesNearbyViewHolderClicks mListener;

        RestaurantNearbyGridViewAdapter gridAdapter;

        // itemView will be my own custom layout View of the row
        public MyViewHolder(Context context, View itemView, CheckinCafesNearbyViewHolderClicks listener) {
            super(itemView);
            this.context = context;
            mListener = listener;

            //Link the objects
            name = (TextView) itemView.findViewById(R.id.name);
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


    }


}
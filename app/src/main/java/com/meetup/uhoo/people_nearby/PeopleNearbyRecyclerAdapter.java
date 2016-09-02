package com.meetup.uhoo.people_nearby;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.meetup.uhoo.R;

import java.util.Collections;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class PeopleNearbyRecyclerAdapter extends RecyclerView.Adapter<PeopleNearbyRecyclerAdapter.MyViewHolder> {

    // emptyList takes care of null pointer exception
    List<PeopleNearbyRecyclerInfo> data = Collections.emptyList();
    LayoutInflater inflator;
    Context context;

    public PeopleNearbyRecyclerAdapter(PeopleNearby context, List<PeopleNearbyRecyclerInfo> data) {
        this.context = context;
        inflator = LayoutInflater.from(context);
        this.data = data;
    }



    public void addRow(PeopleNearbyRecyclerInfo row) {
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

        final View view = inflator.inflate(R.layout.row_people_nearby, parent, false);
        MyViewHolder holder = new MyViewHolder(view, new MyViewHolder.MyViewHolderClicks() {

            public void rowClick(View caller, int position) {
                Log.d("rowClick", "rowClicks");

                //Intent intent = new Intent(context, SendSticker.class);
                //intent.putExtra("userId", data.get(position).getParseObjectId());
                //view.getContext().startActivity(intent);
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
        PeopleNearbyRecyclerInfo current = data.get(position);

        holder.name.setText(current.getName());
        holder.miniBio.setText(current.getMiniBio());


    }


    @Override
    public int getItemCount() {
        return data.size();
    }

    // Created my custom view holder
    public static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView name;
        TextView miniBio;

        CircleImageView profileImage;
        public MyViewHolderClicks mListener;

        // itemView will be my own custom layout View of the row
        public MyViewHolder(View itemView, MyViewHolderClicks listener) {
            super(itemView);

            mListener = listener;

            //Link the objects
            name = (TextView) itemView.findViewById(R.id.name);
            miniBio = (TextView) itemView.findViewById(R.id.miniBio);
            profileImage = (CircleImageView) itemView.findViewById(R.id.profileImage);

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
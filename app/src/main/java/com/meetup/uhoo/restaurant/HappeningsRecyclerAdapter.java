package com.meetup.uhoo.restaurant;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.meetup.uhoo.core.Happening;
import com.meetup.uhoo.R;

import java.util.Collections;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class HappeningsRecyclerAdapter extends RecyclerView.Adapter<HappeningsRecyclerAdapter.MyViewHolder> {

    // emptyList takes care of null pointer exception
    List<Happening> data = Collections.emptyList();
    LayoutInflater inflator;
    Context context;

    public HappeningsRecyclerAdapter(Context context, List<Happening> data) {
        this.context = context;
        inflator = LayoutInflater.from(context);
        this.data = data;
    }



    public void addRow(Happening row) {


        final int index = getItemCount();

        /*
        row.setOnUserDataFetchListener(new UserDataFetchListener() {
            @Override
            public void onUserFetch(User user) {

                Log.d("user Fetch complete", user.getFirstName() + " " + user.getLastName());
                data.set(index,user);
                updateRows();
            }
        });
*/

        data.add(row);
        notifyItemInserted(getItemCount() - 1);
    }

    public void updateRows(){
        notifyDataSetChanged();
    }

    public void clearData() {
        int size = this.data.size();

        data.clear();

        this.notifyItemRangeRemoved(0, size);
    }

    // Called when the recycler view needs to create a new row
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        final View view = inflator.inflate(R.layout.row_happening, parent, false);
        MyViewHolder holder = new MyViewHolder(view, new MyViewHolder.MyViewHolderClicks() {

            public void rowClick(View caller, int position) {
                Log.d("rowClick", "rowClicks");


            }


        });
        return holder;
    }

    // Setting up the data for each row
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        // This gives us current information list object
        Happening current = data.get(position);
        holder.title.setText(current.getTitle());
        holder.description.setText(current.getDescription());

        switch(current.getType()){
            case DEAL:
                holder.tvType.setText("Deal");
                holder.tvType.setTextColor(context.getResources().getColorStateList(R.color.flatRed));
                holder.ivHappeningIcon.setImageResource(R.color.flatRed);
                break;
            case EVENT:
                holder.tvType.setText("Event");
                holder.tvType.setTextColor(context.getResources().getColorStateList(R.color.flatBlue));
                holder.ivHappeningIcon.setImageResource(R.color.flatBlue);
                break;
            case COMEDY:
                holder.tvType.setText("Comedy");
                holder.tvType.setTextColor(context.getResources().getColorStateList(R.color.flatDarkGreen));
                holder.ivHappeningIcon.setImageResource(R.color.flatDarkGreen);
                break;
            case VARIETY:
                holder.tvType.setText("Variety");
                holder.tvType.setTextColor(context.getResources().getColorStateList(R.color.flatYellow));
                holder.ivHappeningIcon.setImageResource(R.color.flatYellow);
                break;
            default:
                break;
        }

    }


    @Override
    public int getItemCount() {
        return data.size();
    }

    // Created my custom view holder
    public static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView title;
        TextView tvType;
        TextView description;
        CircleImageView ivHappeningIcon;

        public MyViewHolderClicks mListener;

        // itemView will be my own custom layout View of the row
        public MyViewHolder(View itemView, MyViewHolderClicks listener) {
            super(itemView);

            mListener = listener;

            //Link the objects
            title = (TextView) itemView.findViewById(R.id.tvTitle);
            description = (TextView) itemView.findViewById(R.id.tvDescription);
            tvType = (TextView) itemView.findViewById(R.id.tvType);
            ivHappeningIcon = (CircleImageView) itemView.findViewById(R.id.ivHappeningIcon);

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
        }
    }


}
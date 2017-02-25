package com.meetup.uhoo.restaurant;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;

import com.meetup.uhoo.credentials.SignIn;
import com.meetup.uhoo.views.ProfileRowView;
import com.meetup.uhoo.R;
import com.meetup.uhoo.core.User;
import com.meetup.uhoo.core.UserDataFetchListener;

import java.util.Collections;
import java.util.List;

public class PeopleNearbyRecyclerAdapter extends RecyclerView.Adapter<PeopleNearbyRecyclerAdapter.MyViewHolder> {

    // emptyList takes care of null pointer exception
    List<User> data = Collections.emptyList();
    LayoutInflater inflator;
    Context context;

    public PeopleNearbyRecyclerAdapter(Context context, List<User> data) {
        this.context = context;
        inflator = LayoutInflater.from(context);
        this.data = data;
    }



    public void addRow(User row) {


        final int index = getItemCount();

        data.add(row);
        notifyItemInserted(index);

        row.setOnUserDataFetchListener(new UserDataFetchListener() {
            @Override
            public void onUserFetch(User user) {

                Log.i("user Fetch complete", user.getFirstName() + " " + user.getLastName());
                data.set(index,user);
                updateRows();
            }
        });


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

        final View view = inflator.inflate(R.layout.row_people_nearby, parent, false);
        MyViewHolder holder = new MyViewHolder(view, new MyViewHolder.MyViewHolderClicks() {

            public void rowClick(View caller, int position) {
                Log.d("rowClick", "rowClicks");


                // Get user auth type. If anon user then tell them to create an account
                SharedPreferences prefs = context.getSharedPreferences("currentUser", context.MODE_PRIVATE);
                String authType = prefs.getString("authType", null);
                if (authType != null && authType.equals("ANON")) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(context);

                    builder.setTitle("Please Create An Account");
                    builder.setMessage("Check into businesses and see user's profiles!");

                    builder.setPositiveButton("Let's Do It!", new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {

                            Intent intent = new Intent(context, SignIn.class);
                            dialog.dismiss();

                            context.startActivity(intent);
                        }
                    });

                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

                    AlertDialog alert = builder.create();
                    alert.show();

                } else {

                    ProfileRowView profileRowView = (ProfileRowView) caller.findViewById(R.id.prProfileRow);


                    UserProfileDialog userDialog = new UserProfileDialog((RestaurantActivity) context, data.get(position));
                    userDialog.show();
                    Window window = userDialog.getWindow();
                    window.setLayout(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
                }




            }

        });
        return holder;
    }

    // Setting up the data for each row
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        // This gives us current information list object
        User current = data.get(position);
        holder.prProfileRow.setData(current);
    }


    @Override
    public int getItemCount() {
        return data.size();
    }

    // Created my custom view holder
    public static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {


        private ProfileRowView prProfileRow;
        public MyViewHolderClicks mListener;

        // itemView will be my own custom layout View of the row
        public MyViewHolder(View itemView, MyViewHolderClicks listener) {
            super(itemView);

            mListener = listener;

            prProfileRow = (ProfileRowView) itemView.findViewById(R.id.prProfileRow);




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
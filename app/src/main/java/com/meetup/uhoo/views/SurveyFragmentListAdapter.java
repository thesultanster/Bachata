package com.meetup.uhoo.views;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.meetup.uhoo.R;
import com.meetup.uhoo.core.Happening;
import com.meetup.uhoo.core.SurveyOption;

import java.util.Collections;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class SurveyFragmentListAdapter extends RecyclerView.Adapter<SurveyFragmentListAdapter.MyViewHolder> {

    // emptyList takes care of null pointer exception
    List<SurveyOption> data = Collections.emptyList();
    LayoutInflater inflator;
    Context context;
    SurveyAnswerInterface surveyAnswerInterface;

    public SurveyFragmentListAdapter(Context context, List<SurveyOption> data, SurveyAnswerInterface surveyAnswerInterface) {
        this.context = context;
        inflator = LayoutInflater.from(context);
        this.data = data;
        this.surveyAnswerInterface = surveyAnswerInterface;
    }



    public void addRow(String row) {


        final int index = getItemCount();

        //TODO: WRONG COMPLETE THIS
        //data.add(row);
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

        final View view = inflator.inflate(R.layout.row_fragment_survey, parent, false);
        MyViewHolder holder = new MyViewHolder(view, new MyViewHolder.MyViewHolderClicks() {

            public void rowClick(View caller, int position) {
                Log.d("rowClick", "rowClicks");

                // When answer is clicked, we let the SurveyFragment know
                surveyAnswerInterface.onSingleAnswerSelected(position);
            }


        });
        return holder;
    }

    // Setting up the data for each row
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        // This gives us current information list object
        holder.tvAnswer.setText(data.get(position).getTitle());

    }


    @Override
    public int getItemCount() {
        try{
            return data.size();
        }
        catch (Exception e){

        }

        return 0;

    }

    // Created my custom view holder
    public static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView tvAnswer;

        public MyViewHolderClicks mListener;

        // itemView will be my own custom layout View of the row
        public MyViewHolder(View itemView, MyViewHolderClicks listener) {
            super(itemView);

            mListener = listener;

            //Link the objects
            tvAnswer = (TextView) itemView.findViewById(R.id.tvAnswer);

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
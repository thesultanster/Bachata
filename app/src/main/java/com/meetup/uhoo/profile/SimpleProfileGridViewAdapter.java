package com.meetup.uhoo.profile;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.meetup.uhoo.R;

import java.util.ArrayList;

public class SimpleProfileGridViewAdapter extends ArrayAdapter {
    private Context context;
    private int layoutResourceId;
    private ArrayList<SimpleProfileActivityItem> data = new ArrayList<SimpleProfileActivityItem>();
    private ArrayList<String> selectedList = new ArrayList<String>();

    public SimpleProfileGridViewAdapter(Context context, int layoutResourceId, ArrayList<SimpleProfileActivityItem> data) {
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
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            holder = new ViewHolder();
            holder.imageTitle = (TextView) row.findViewById(R.id.tvActivity);
            holder.image = (ImageView) row.findViewById(R.id.ivActivityIcon);
            holder.llGridItem = (LinearLayout) row.findViewById(R.id.llgridItemActivity);
            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }

        SimpleProfileActivityItem item = data.get(position);
        holder.selected = item.getSelected();
        holder.imageTitle.setText(item.getTitle());
        holder.image.setImageBitmap(item.getImage());

        // If selected
        if(item.getSelected()){
            holder.llGridItem.setBackgroundColor(Color.parseColor("#232323"));
        } else {
            holder.llGridItem.setBackgroundColor(Color.parseColor("#9A9A9A"));
        }

        return row;
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    static class ViewHolder {
        TextView imageTitle;
        ImageView image;
        boolean selected;
        LinearLayout llGridItem;
    }

    public void SetSelected(int position){
        if(data.get(position).getSelected()){
            data.get(position).setSelected(false);
            selectedList.remove(data.get(position).getTitle());
        } else {
            data.get(position).setSelected(true);
            selectedList.add(data.get(position).getTitle());
        }
    }

    public ArrayList<String> getSelectedItems(){
        return selectedList;
    }

    public String getSelectedName(int position){
        return data.get(position).getTitle();
    }

}
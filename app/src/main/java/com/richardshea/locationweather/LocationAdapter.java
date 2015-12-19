package com.richardshea.locationweather;

import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by richardshea on 11/7/15.
 */
public class LocationAdapter extends BaseAdapter {

    Context mContext;

    private LayoutInflater mLayoutInflater;

    private List<String> mEntries = new ArrayList<String>();


    public LocationAdapter(Context context) {

        mContext = context;

        mLayoutInflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {

        return mEntries.size();
    }

    @Override
    public String getItem(int position) {

        return mEntries.get(position);
    }

    @Override
    public long getItemId(int position) {

        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        RelativeLayout itemView;
        if(convertView == null) {
            itemView = (RelativeLayout) mLayoutInflater.inflate(R.layout.list_locations, parent, false);
        } else {
            itemView = (RelativeLayout ) convertView;
        }

        TextView textView = (TextView) itemView.findViewById(R.id.city_state);

        String title = mEntries.get(position);
        textView.setText(title);

        return itemView;
    }

    public void upDateEntries(List<String> entries) {

        mEntries = entries;
        notifyDataSetChanged();
    }
}

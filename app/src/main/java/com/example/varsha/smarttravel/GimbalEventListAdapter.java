package com.example.varsha.smarttravel;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;

public class GimbalEventListAdapter extends BaseAdapter {
    private Activity activity;
    private List<String> events = new ArrayList<>();

    public GimbalEventListAdapter(Activity activity) {
        this.activity = activity;
    }

    public void setEvents(List<String> events) {
        this.events.clear();
        this.events.addAll(events);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return events.size();
    }

    @Override
    public Object getItem(int position) {
        return events.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String event = events.get(position);
        View view = convertView;
        if (view == null) {
            view = activity.getLayoutInflater().inflate(R.layout.gimbal_list_item, null);
        }

        TextView title = (TextView) view.findViewById(R.id.title);
        title.setText(event);

        return view;
    }
}

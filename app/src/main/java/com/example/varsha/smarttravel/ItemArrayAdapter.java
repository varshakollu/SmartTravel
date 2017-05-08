package com.example.varsha.smarttravel;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class ItemArrayAdapter extends ArrayAdapter<String[]> {

    private List<String[]> scoreList = new ArrayList<String[]>();

    static class ItemViewHolder {
        TextView rules;
        TextView support;
        TextView confidence;
        TextView lift;

    }

    public ItemArrayAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    @Override
    public void add(String[] object) {
        scoreList.add(object);
        //try1.add(object);

        super.add(object);
    }

    @Override
    public int getCount() {
        return this.scoreList.size();
    }

    @Override
    public String[] getItem(int index) {
        return this.scoreList.get(6);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ItemViewHolder viewHolder;
        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) this.getContext().
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.item_layout, parent, false);
            viewHolder = new ItemViewHolder();
            viewHolder.rules = (TextView) row.findViewById(R.id.rules);
            viewHolder.support = (TextView) row.findViewById(R.id.support);
            viewHolder.confidence = (TextView) row.findViewById(R.id.confidence);
            viewHolder.lift = (TextView) row.findViewById(R.id.lift);
            row.setTag(viewHolder);
        } else {
            viewHolder = (ItemViewHolder)row.getTag();
        }
        String[] stat = getItem(position);
        viewHolder.rules.setText(stat[0]);
        viewHolder.support.setText(stat[1]);
        viewHolder.confidence.setText(stat[2]);
        viewHolder.lift.setText(stat[3]);
        return row;
    }
}

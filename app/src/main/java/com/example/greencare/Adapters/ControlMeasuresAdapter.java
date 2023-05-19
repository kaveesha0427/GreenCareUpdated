package com.example.greencare.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.greencare.R;

import java.util.ArrayList;


public class ControlMeasuresAdapter extends BaseAdapter {

    Context context;
    ArrayList<String> suggestionList;
    LayoutInflater inflater;


    public ControlMeasuresAdapter(Context context, ArrayList<String> suggestionList) {
        this.context = context;
        this.suggestionList = suggestionList;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return suggestionList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = inflater.inflate(R.layout.list_items, null);
        TextView textView = (TextView) convertView.findViewById(R.id.list_item);
        textView.setText(suggestionList.get(position));
        return convertView;
    }
}

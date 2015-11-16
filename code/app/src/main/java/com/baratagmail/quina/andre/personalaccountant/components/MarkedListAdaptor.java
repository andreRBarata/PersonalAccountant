package com.baratagmail.quina.andre.personalaccountant.components;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.baratagmail.quina.andre.personalaccountant.R;

import java.util.TreeMap;


/**
 * Created by andre on 08-11-2015.
 */
public class MarkedListAdaptor extends ArrayAdapter<TreeMap<String,String>> {
    public MarkedListAdaptor(Context context) {
        super(context, R.layout.marked_row);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        //return super.getView(position, convertView, parent);

        View row = convertView;

        if(row==null){
            LayoutInflater inflater = LayoutInflater.from(super.getContext());
            row=inflater.inflate(R.layout.marked_row, parent, false);
        }

        TextView label=(TextView)row.findViewById(R.id.text);
        label.setText(getItem(position).get("label"));

        TextView budget=(TextView)row.findViewById(R.id.budget);
        budget.setText(getItem(position).get("budget"));

        TextView usage=(TextView)row.findViewById(R.id.usage);
        usage.setText(getItem(position).get("usage"));

        return row;
    }
}

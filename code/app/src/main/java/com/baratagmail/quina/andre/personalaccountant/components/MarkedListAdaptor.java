package com.baratagmail.quina.andre.personalaccountant.components;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.baratagmail.quina.andre.personalaccountant.R;

import org.xmlpull.v1.XmlPullParser;

import java.util.TreeMap;


/**
 * Created by andre on 08-11-2015.
 */
public class MarkedListAdaptor extends ArrayAdapter<TreeMap<String,String>> {
    private int row_layout;

    public MarkedListAdaptor(Context context, int resource, int row_layout) {
        super(context, resource);

        this.row_layout = row_layout;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        //return super.getView(position, convertView, parent);

        View row = convertView;
        TreeMap<String,String> item = getItem(position);

        if(row==null){
            LayoutInflater inflater = LayoutInflater.from(super.getContext());
            row=inflater.inflate(row_layout, parent, false);
        }

        for (String key: item.keySet()) {
            int id = row.getResources().getIdentifier(key,
                    "id",
                    row.getContext().getPackageName()
            );
            if (id != 0) {
                TextView label = (TextView) row.findViewById(id);
                label.setText(getItem(position).get(key));
            }
        }

        return row;
    }
}

package com.baratagmail.quina.andre.personalaccountant.components;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baratagmail.quina.andre.personalaccountant.R;

import java.util.TreeMap;


/**
 * Created by andre on 08-11-2015.
 *
 * Used to fill the categories for the Main activity
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
        if (getItem(position).containsKey("text")) {
            label.setText(getItem(position).get("text"));
        }

        TextView budget=(TextView)row.findViewById(R.id.budget);
        if (getItem(position).containsKey("budget")) {
            budget.setText(getItem(position).get("budget"));
        }

        TextView usage=(TextView)row.findViewById(R.id.usage);
        if (getItem(position).containsKey("usage")) {
            usage.setText(getItem(position).get("usage"));
        }

        ImageView image = (ImageView)row.findViewById(R.id.category_image);

        if (getItem(position).containsKey("image_path") && getItem(position).get("image_path") != null) {
            image.setImageResource(
                    row.getResources().getIdentifier(
                            getItem(position).get("image_path"),
                            "drawable",
                            getContext().getPackageName()
                    )
            );
        }

        return row;
    }
}

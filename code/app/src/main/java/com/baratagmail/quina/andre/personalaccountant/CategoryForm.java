package com.baratagmail.quina.andre.personalaccountant;

import android.app.ActionBar;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Gallery;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import com.baratagmail.quina.andre.personalaccountant.components.FormPair;
import com.baratagmail.quina.andre.personalaccountant.database.DBManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.TreeMap;

/**
 * Created by andre on 17-11-2015.
 */
public class CategoryForm extends AppCompatActivity implements View.OnClickListener {
    private String id;
    private EditText category_name;
    private EditText category_budget;
    private String category_imagepath;
    private Spinner counting_period;
    private ImageView category_image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.category_form);

        Button category_save = (Button) findViewById(R.id.category_save);
        Button category_delete = (Button) findViewById(R.id.category_cancel);
        category_image = (ImageView) findViewById(R.id.category_image);

        category_name = (EditText)
                findViewById(R.id.category_name);
        category_budget = (EditText)
                findViewById(R.id.category_budget);;
        counting_period = (Spinner)
                findViewById(R.id.counting_period);


        category_save.setOnClickListener(this);
        category_delete.setOnClickListener(this);
        category_image.setOnClickListener(this);

        ArrayAdapter<FormPair> counts =
                new ArrayAdapter<FormPair>(
                        counting_period.getContext(),
                        android.R.layout.simple_spinner_dropdown_item
                );


        counts.addAll(
            new FormPair(
                    "id", String.valueOf(Calendar.DAY_OF_WEEK),
                    "name", "Weekly"
            ),
            new FormPair(
                    "id", String.valueOf(Calendar.DAY_OF_MONTH),
                    "name", "Monthly"
            ),
            new FormPair(
                    "id", String.valueOf(Calendar.DAY_OF_YEAR),
                    "name", "Yearly"
            )
        );

        counting_period.setAdapter(counts);

        //Adding values for edit mode
        if (getIntent().getStringExtra("id") != null) {
            int count = 0;
            DBManager db = new DBManager(getBaseContext());
            Cursor cursor;

            id = getIntent().getStringExtra("id");

            db.open();

            cursor = db.select("Category",
                    new String[]{"name", "budget", "counting_period", "image_path"},
                    "id = ?",
                    Arrays.asList(id)
            );


            category_name.setText(cursor.getString(0));
            category_budget.setText(
                    String.valueOf(cursor.getFloat(1))
            );

            while (count < counting_period.getCount()) {
                FormPair item = (FormPair)counting_period
                        .getItemAtPosition(count);

                if (item.get("id") == String.valueOf(cursor.getInt(2))) {
                    break;
                }

                count++;
            }

            if (count < counting_period.getCount()) {
                counting_period.setSelection(count);
            }

            if (!cursor.isNull(3) && !cursor.getString(3).isEmpty()) {
                category_imagepath = cursor.getString(3);
                category_image.setImageResource(
                        getResources().getIdentifier(cursor.getString(3),
                                "drawable",
                                getPackageName()
                        )
                );
            }
            cursor.close();
            db.close();
        }

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.category_cancel) {
                finish();
        }
        else if (v.getId() == R.id.category_save) {
            DBManager db = new DBManager(getBaseContext());
            ContentValues values = new ContentValues();

            values.put("name",
                    category_name.getText().toString()
            );
            values.put("budget",
                    category_budget.getText().toString()
            );
            values.put("image_path",
                    category_imagepath
            );
            values.put("counting_period",
                    ((FormPair)
                            counting_period.getSelectedItem()
                    ).get("id")
            );

            if (id == null) {
                db.open();
                db.insert("Category", values);
                db.close();

                finish();
            }
            else {
                db.open();
                db.update("Category", values,
                        "id = ?",
                        Arrays.asList(id)
                );
                db.close();

                finish();
            }
        }
        else if (v.getId() == R.id.category_image) {
            GridLayout layout = new GridLayout(getBaseContext());
            GridLayout.LayoutParams param = new GridLayout.LayoutParams();

            param.width = GridLayout.LayoutParams.MATCH_PARENT;
            param.setGravity(Gravity.CENTER);

            layout.setLayoutParams(param);
            layout.setColumnCount(4);



            for (final String icon: getResources().getStringArray(R.array.icons)) {
                ImageView view = new ImageView(layout.getContext());
                final int resource = getResources().getIdentifier(icon,
                        "drawable",
                        getPackageName()
                );

                view.setImageResource(
                        resource
                );

                view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            category_image.setImageResource(resource);
                            category_imagepath = icon;
                        }
                    }
                );

                layout.addView(view);
            }

            new AlertDialog.Builder(this).setView(layout)
                    .setPositiveButton(R.string.ok, null).show();
        }
    }
}

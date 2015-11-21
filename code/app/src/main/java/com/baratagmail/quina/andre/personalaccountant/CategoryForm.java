package com.baratagmail.quina.andre.personalaccountant;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import com.baratagmail.quina.andre.personalaccountant.components.FormPair;
import com.baratagmail.quina.andre.personalaccountant.database.DBManager;

import java.util.TreeMap;

/**
 * Created by andre on 17-11-2015.
 */
public class CategoryForm extends AppCompatActivity implements View.OnClickListener {
    private String id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.category_form);
        EditText category_name = (EditText)
                findViewById(R.id.category_name);
        EditText category_budget = (EditText)
                findViewById(R.id.category_budget);
        EditText category_imagepath = (EditText)
                findViewById(R.id.category_imagepath);
        Spinner counting_period = (Spinner)
                findViewById(R.id.counting_period);
        Button category_save = (Button) findViewById(R.id.category_save);
        Button category_delete = (Button) findViewById(R.id.category_cancel);
        ImageView category_image = (ImageView) findViewById(R.id.category_image);

        category_save.setOnClickListener(this);
        category_delete.setOnClickListener(this);

        ArrayAdapter<FormPair> counts =
                new ArrayAdapter<FormPair>(
                        counting_period.getContext(),
                        android.R.layout.simple_spinner_dropdown_item
                );

        counts.addAll(
                new FormPair(
                        "id", "0",
                        "name", "Weekly"
                ),
                new FormPair(
                        "id", "1",
                        "name", "Monthly"
                ),
                new FormPair(
                        "id", "2",
                        "name", "Yearly"
                )
        );

        counting_period.setAdapter(counts);

        //Adding values for edit mode
        if (getIntent().getStringExtra("id") != null) {
            DBManager db = new DBManager(getBaseContext());
            Cursor cursor;

            id = getIntent().getStringExtra("id");

            db.open();

            cursor = db.select("Category",
                    new String[]{"name", "budget", "counting_period", "image_path"},
                    "id = ?",
                    id
            );


            category_name.setText(cursor.getString(0));
            category_budget.setText(
                    String.valueOf(cursor.getFloat(1))
            );

            counting_period.setSelection(cursor.getInt(2));

            if (!cursor.isNull(3) && !cursor.getString(3).isEmpty()) {
                category_imagepath.setText(cursor.getString(3));
                category_image.setImageBitmap(
                    BitmapFactory.decodeFile(
                            cursor.getString(3)
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
            EditText category_name = (EditText)
                    findViewById(R.id.category_name);
            EditText category_budget = (EditText)
                    findViewById(R.id.category_budget);
            EditText category_imagepath = (EditText)
                    findViewById(R.id.category_imagepath);
            Spinner counting_period = (Spinner)
                    findViewById(R.id.counting_period);
            DBManager db = new DBManager(getBaseContext());
            ContentValues values = new ContentValues();

            values.put("name",
                    category_name.getText().toString()
            );
            values.put("budget",
                    category_budget.getText().toString()
            );
            values.put("image_path",
                    category_imagepath.getText().toString()
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
                    new String[] {id}
                );
                db.close();

                finish();
            }
        }
    }
}

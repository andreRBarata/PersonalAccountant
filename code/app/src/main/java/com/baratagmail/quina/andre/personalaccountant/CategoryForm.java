package com.baratagmail.quina.andre.personalaccountant;

import android.content.ContentValues;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import com.baratagmail.quina.andre.personalaccountant.components.FormPair;
import com.baratagmail.quina.andre.personalaccountant.database.DBManager;

import java.util.TreeMap;

/**
 * Created by andre on 17-11-2015.
 */
public class CategoryForm extends AppCompatActivity implements View.OnClickListener {
    private TreeMap<String,String> category;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.category_form);
        Spinner counting_period = (Spinner) findViewById(R.id.counting_period);
        Button category_save = (Button) findViewById(R.id.category_save);
        Button category_delete = (Button) findViewById(R.id.category_delete);

        category_save.setOnClickListener(this);
        category_delete.setOnClickListener(this);

        ArrayAdapter<FormPair> counts =
                new ArrayAdapter<FormPair>(
                        counting_period.getContext(),
                        android.R.layout.simple_spinner_dropdown_item
                );

        counts.addAll(
                new FormPair(
                        "id", "1",
                        "name", "Weekly"
                ),
                new FormPair(
                        "id", "2",
                        "name", "Monthly"
                ),
                new FormPair(
                        "id", "3",
                        "name", "Yearly"
                )
        );

        counting_period.setAdapter(counts);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.category_delete) {
            //If delete button is clicked and form not editing
            if (category == null) {
                finish();
            }
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

            if (category == null) {
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

                db.open();

                db.insert("Category", values);

                db.close();

                finish();
            }
        }
    }
}

package com.baratagmail.quina.andre.personalaccountant;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.Spinner;

import com.baratagmail.quina.andre.personalaccountant.components.FormPair;
import com.baratagmail.quina.andre.personalaccountant.database.DBManager;

import java.util.Arrays;
import java.util.Calendar;

/**
 * Created by andre on 17-11-2015.
 *
 * Allows the creation and edition of categories
 */
public class CategoryForm extends AppCompatActivity implements View.OnClickListener {
    private String id;
    private EditText categoryName;
    private EditText categoryBudget;
    private String categoryImagepath;
    private Spinner countingPeriod;
    private ImageView categoryImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.category_form);

        Button categorySave = (Button) findViewById(R.id.category_save);
        Button categoryDelete = (Button) findViewById(R.id.category_cancel);
        categoryImage = (ImageView) findViewById(R.id.category_image);

        categoryImagepath = "image";

        categoryName = (EditText)
                findViewById(R.id.category_name);
        categoryBudget = (EditText)
                findViewById(R.id.category_budget);
        countingPeriod = (Spinner)
                findViewById(R.id.counting_period);


        categorySave.setOnClickListener(this);
        categoryDelete.setOnClickListener(this);
        categoryImage.setOnClickListener(this);

        ArrayAdapter<FormPair> counts =
                new ArrayAdapter<FormPair>(
                        countingPeriod.getContext(),
                        android.R.layout.simple_spinner_dropdown_item
                );

        //Setting counting periods from java Calendar magic numbers
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

        countingPeriod.setAdapter(counts);

        //Adding values for edit mode
        if (getIntent().getStringExtra("id") != null) {
            int count = 0;
            DBManager db = new DBManager(getBaseContext());
            Cursor cursor;

            id = getIntent().getStringExtra("id");

            db.open();

            cursor = db.select("Category",
                    new String[]{"name", "current_budget", "counting_period", "image_path"},
                    "id = ?",
                    Arrays.asList(id)
            );


            categoryName.setText(cursor.getString(0));
            categoryBudget.setText(
                    String.valueOf(cursor.getFloat(1))
            );

            while (count < countingPeriod.getCount()) {
                FormPair item = (FormPair) countingPeriod
                        .getItemAtPosition(count);

                if (item.get("id").equals(String.valueOf(cursor.getInt(2)))) {
                    break;
                }

                count++;
            }

            if (count < countingPeriod.getCount()) {
                countingPeriod.setSelection(count);
            }

            //If image if not empty
            if (!cursor.isNull(3) && !cursor.getString(3).isEmpty()) {
                categoryImagepath = cursor.getString(3);
                categoryImage.setImageResource(
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
                    categoryName.getText().toString()
            );
            values.put("current_budget",
                    categoryBudget.getText().toString()
            );
            values.put("image_path",
                    categoryImagepath
            );
            values.put("counting_period",
                    ((FormPair)
                            countingPeriod.getSelectedItem()
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

            layout.setLayoutParams(param);
            layout.setColumnCount(4);


            //Images from: https://www.google.com/design/icons/index.html
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
                            categoryImage.setImageResource(resource);
                            categoryImagepath = icon;
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

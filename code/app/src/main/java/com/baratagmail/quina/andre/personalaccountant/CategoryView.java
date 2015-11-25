package com.baratagmail.quina.andre.personalaccountant;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.baratagmail.quina.andre.personalaccountant.components.FormPair;
import com.baratagmail.quina.andre.personalaccountant.database.DBManager;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

/**
 * Created by andre on 21-11-2015.
 */
public class CategoryView extends AppCompatActivity {
    private String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.category_view);

        TextView budget = (TextView) findViewById(R.id.category_budget);
        TextView spent = (TextView) findViewById(R.id.category_spent);
        TextView tally = (TextView) findViewById(R.id.category_tally);
        ImageView image = (ImageView) findViewById(R.id.category_image);
        Intent intent = getIntent();
        DBManager db = new DBManager(getBaseContext());

        ListView receiptsView = (ListView) findViewById(R.id.receipts);

        ArrayList<FormPair> receipts =
                new ArrayList<FormPair>();
        Cursor categoryCursor;
        Cursor receiptsCursor;
        String last_reset;
        String next_reset;

        id = intent.getStringExtra("id");

        db.open();

        categoryCursor = db.select(
                "Category_xp",
                new String[]{
                        "name",
                        "total",
                        "budget",
                        "image_path",
                        "last_reset",
                        "next_reset"
                },
                "id = ?",
                Arrays.asList(id)
        );

        setTitle(categoryCursor.getString(0));

        last_reset = categoryCursor.getString(4);
        next_reset = categoryCursor.getString(5);

        budget.setText(
                "€" + String.valueOf(categoryCursor.getFloat(2)) + " budget"
        );
        spent.setText(
                "€" + String.valueOf(categoryCursor.getFloat(1)) + " spent"
        );
        tally.setText(
                "between " + last_reset.split(" ")[0] + " and " + next_reset.split(" ")[0]
        );

        image.setImageResource(
                getResources().getIdentifier(
                        categoryCursor.getString(3),
                        "drawable",
                        getPackageName()
                )
        );

        if (last_reset != null && next_reset != null) {
            receiptsCursor = db.select(
                    "select date_recorded, cost from Receipt "
                            + "where date_recorded between ? and ?",
                    Arrays.asList(last_reset, next_reset)
            );

            while (!receiptsCursor.isAfterLast()) {
                Log.d("field", receiptsCursor.getString(0));
                receipts.add(
                        new FormPair(
                                "text1", receiptsCursor.getString(0),
                                "text2", "€" + receiptsCursor.getString(1)
                        )
                );

                receiptsCursor.moveToNext();
            }

            receiptsCursor.close();
        }
        else {
            Toast.makeText(this, R.string.nothingfound_error, Toast.LENGTH_LONG).show();
        }




        categoryCursor.close();
        db.close();

        receiptsView.setAdapter(
                new SimpleAdapter(
                        receiptsView.getContext(),
                        receipts,
                        R.layout.receipt_row,
                        new String[]{"text1", "text2"},
                        new int[]{R.id.text1, R.id.text2}
                )
        );
    }
}

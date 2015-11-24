package com.baratagmail.quina.andre.personalaccountant;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.baratagmail.quina.andre.personalaccountant.components.FormPair;
import com.baratagmail.quina.andre.personalaccountant.database.DBManager;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by andre on 21-11-2015.
 */
public class CategoryView extends AppCompatActivity {
    private String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.category_view);
        Intent intent = getIntent();
        DBManager db = new DBManager(getBaseContext());

        ListView receipts_view = (ListView) findViewById(R.id.receipts);

        ArrayList<FormPair> receipts =
                new ArrayList<FormPair>();
        Cursor category_cursor;
        Cursor receipts_cursor;

        id = intent.getStringExtra("id");

        db.open();

        category_cursor = db.select(
                "Category_xp",
                new String[]{"name", "total"},
                "id = ?",
                Arrays.asList(id)
        );

        setTitle(category_cursor.getString(0));

        receipts_cursor = db.select(
                "Receipt",
                new String[]{"date_recorded", "cost"},
                "category_id = ?",
                Arrays.asList(id)
        );


        while (!receipts_cursor.isAfterLast()) {
            Log.d("field", receipts_cursor.getString(0));
            receipts.add(
                new FormPair(
                        "text1", receipts_cursor.getString(0),
                        "text2", "€" + receipts_cursor.getString(1)
                )
            );

            receipts_cursor.moveToNext();
        }

        category_cursor.close();
        receipts_cursor.close();
        db.close();

        receipts_view.setAdapter(
                new SimpleAdapter(
                        receipts_view.getContext(),
                        receipts,
                        android.R.layout.simple_list_item_2,
                        new String[]{"text1","text2"},
                        new int[]{android.R.id.text1, android.R.id.text2}
                )
        );
    }
}

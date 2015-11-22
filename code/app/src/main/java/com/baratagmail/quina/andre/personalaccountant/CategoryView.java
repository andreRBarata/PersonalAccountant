package com.baratagmail.quina.andre.personalaccountant;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.baratagmail.quina.andre.personalaccountant.database.DBManager;

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
        Cursor cursor;

        id = intent.getStringExtra("id");

        db.open();

        cursor = db.select(
                "Category",
                new String[]{"name"},
                "id = ?",
                Arrays.asList(id)
        );

        setTitle(cursor.getString(0));

        cursor.close();
        db.close();


    }
}

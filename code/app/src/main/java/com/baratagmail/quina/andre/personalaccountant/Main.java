package com.baratagmail.quina.andre.personalaccountant;


import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.baratagmail.quina.andre.personalaccountant.components.FormPair;
import com.baratagmail.quina.andre.personalaccountant.components.MarkedListAdaptor;
import com.baratagmail.quina.andre.personalaccountant.database.DBManager;

import java.io.File;


public class Main extends AppCompatActivity implements View.OnClickListener {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_categories);
        super.setTitle("Categories");

        ListView list = (ListView)findViewById(R.id.categories);
        Button addReceipt = (Button)findViewById(R.id.add_receipt);
        Button addCategory = (Button)findViewById(R.id.add_category);


        //Adding categories to the list
        MarkedListAdaptor categories =
                new MarkedListAdaptor(
                        list.getContext()
                );
        DBManager db = new DBManager(getBaseContext());
        Cursor cursor;

        db.open();

        cursor = db.select("Category", new String[] {"name", "budget"});

        while (!cursor.isAfterLast()) {
            Log.d("db", cursor.getString(0));
            categories.add(
                new FormPair(
                        "label", cursor.getString(0),
                        "budget", ("€" + cursor.getFloat(1)),
                        "usage", "testetestestes"
                )
            );

            cursor.moveToNext();
        }

        list.setAdapter(categories);

        db.close();

        //Setting action of clicking addReceipt and addCategory
        addReceipt.setOnClickListener(
            this
        );
        addCategory.setOnClickListener(
            this
        );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;

        if (v.getId() == R.id.add_receipt) {
            intent = new Intent(this, Receipt.class);
        }
        else if (v.getId() == R.id.add_category) {
            intent = new Intent(this, CategoryForm.class);
        }

        if (intent != null) {
            startActivity(intent);
        }
    }
}

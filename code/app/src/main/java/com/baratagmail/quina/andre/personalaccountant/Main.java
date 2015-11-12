package com.baratagmail.quina.andre.personalaccountant;

import android.app.Activity;
import android.app.ListActivity;
import android.database.Cursor;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.baratagmail.quina.andre.personalaccountant.components.MarkedListAdaptor;
import com.baratagmail.quina.andre.personalaccountant.database.DBManager;

import java.util.Map;
import java.util.TreeMap;

public class Main extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_categories);
        super.setTitle("Categories");
        ListView list = (ListView)findViewById(R.id.categories);



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
                new String[]{
                        cursor.getString(0),
                        "€" + cursor.getFloat(1)
                }
            );

            cursor.moveToNext();
        }

        list.setAdapter(categories);

        db.close();
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
}

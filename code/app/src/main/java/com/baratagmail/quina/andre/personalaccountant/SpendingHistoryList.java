package com.baratagmail.quina.andre.personalaccountant;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.baratagmail.quina.andre.personalaccountant.components.FormPair;
import com.baratagmail.quina.andre.personalaccountant.database.DBManager;
import java.util.Arrays;

/**
 * Created by andre on 26-11-2015.
 *
 * This class alows the user to easily search through
 * all the past receipts
 */
public class SpendingHistoryList extends ListActivity {
    private String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        DBManager db = new DBManager(getBaseContext());
        Cursor categoryCursor;
        ArrayAdapter<String> categoryList = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1
        );
        int count = 0;

        id = intent.getStringExtra("id");

        db.open();

        categoryCursor = db.select(
                "SpendingHistory_xp",
                new String[]{
                        "start_date",
                        "end_date"
                },
                "category_id = ?",
                Arrays.asList(id)
        );

        while (!categoryCursor.isAfterLast()) {

            categoryList.add(
                    categoryCursor.getString(0).split(" ")[0]
                            + " to " +
                    categoryCursor.getString(1).split(" ")[0]
            );
            count++;
            categoryCursor.moveToNext();
        }
        categoryCursor.close();
        db.close();

        setListAdapter(categoryList);
    }

    @Override
    public void onListItemClick(ListView parent, View v, int position, long rowid) {
        super.onListItemClick(parent, v, position, rowid);
        Intent intent = new Intent(this, CategoryView.class);

        intent.putExtra("id", id);
        intent.putExtra("page", position);
        intent.putExtra("outsider", true);

        startActivity(intent);
    }
}

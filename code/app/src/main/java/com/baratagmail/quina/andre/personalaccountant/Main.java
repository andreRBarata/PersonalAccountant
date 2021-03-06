package com.baratagmail.quina.andre.personalaccountant;


import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.baratagmail.quina.andre.personalaccountant.components.FormPair;
import com.baratagmail.quina.andre.personalaccountant.components.MarkedListAdaptor;
import com.baratagmail.quina.andre.personalaccountant.database.DBManager;

/**
 * Created by andre on 02-11-2015.
 *
 * Main page shows the list of different categories and has buttons
 * for creating categories and receipts
 */

public class Main extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener {
    private final static int CATEGORY_EDIT = 0;
    private final static int CATEGORY_DELETE = 1;
    private final static int CATEGORY_HISTORY = 2;
    private ListView categories;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_categories);

        Button addReceipt = (Button)findViewById(R.id.add_receipt);
        Button addCategory = (Button)findViewById(R.id.add_category);
        categories = (ListView)findViewById(R.id.categories);

        registerForContextMenu(categories);

        setCategoryList();

        //Setting action of clicking addReceipt and addCategory
        addReceipt.setOnClickListener(
            this
        );
        addCategory.setOnClickListener(
                this
        );
        categories.setOnItemClickListener(this);
    }

    protected void onResume() {
        super.onResume();

        setCategoryList();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, CATEGORY_EDIT, 0, R.string.edit);
        menu.add(0, CATEGORY_DELETE, 0, R.string.delete);
        menu.add(0, CATEGORY_HISTORY, 0, R.string.see_history);
    }

    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)
                item.getMenuInfo();
        final int index = info.position;
        final String id = ((FormPair)categories.getItemAtPosition(index)).get("id");

        if (item.getItemId() == CATEGORY_EDIT) {
            Intent intent = new Intent(this, CategoryForm.class);

            intent.putExtra("id",
                    id
            );

            startActivity(intent);
        }
        else if (item.getItemId() == CATEGORY_DELETE) {
            new AlertDialog.Builder(this)
                    .setMessage("This will delete all associated receipts\nAre you sure?")
                    .setPositiveButton("Yes",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    DBManager db = new DBManager(getBaseContext());
                                    db.open();
                                    db.deleteCategory(id);
                                    setCategoryList();
                                    db.close();
                                }
                            })
                    .setNegativeButton("No", null).show();
        }
        else if (item.getItemId() == CATEGORY_HISTORY) {
            Intent intent = new Intent(this, SpendingHistoryList.class);

            intent.putExtra("id", id);

            startActivity(intent);
        }
        return true;
    }



    private void setCategoryList() {
        ListView list = (ListView)findViewById(R.id.categories);
        //Adding categories to the list
        MarkedListAdaptor categories =
                new MarkedListAdaptor(
                        list.getContext()
                );
        DBManager db = new DBManager(getBaseContext());
        Cursor cursor;

        db.open();

        cursor = db.select(
                "Category_xp",
                new String[]{"id", "name", "budget", "total", "image_path"}
        );

        if (cursor.getCount() <= 0) {
            findViewById(R.id.add_receipt).setVisibility(View.INVISIBLE);
        }
        else {
            findViewById(R.id.add_receipt).setVisibility(View.VISIBLE);
        }

        while (!cursor.isAfterLast()) {

            categories.add(
                    new FormPair(
                            "id", String.valueOf(cursor.getInt(0)),
                            "text", cursor.getString(1),
                            "budget", ("€" + cursor.getFloat(2) + " budget"),
                            "usage", ("€" + cursor.getFloat(3) + " spent"),
                            "image_path", cursor.getString(4),
                            "color", String.valueOf(Color.BLUE)
                    )
            );

            cursor.moveToNext();
        }

        list.setAdapter(categories);
        cursor.close();
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

    @Override
    public void onClick(View v) {
        Intent intent = null;

        if (v.getId() == R.id.add_receipt) {
            intent = new Intent(this, ReceiptForm.class);
        }
        else if (v.getId() == R.id.add_category) {
            intent = new Intent(this, CategoryForm.class);
        }

        if (intent != null) {
            startActivity(intent);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(this, CategoryView.class);
        FormPair selected = (FormPair) parent.getItemAtPosition(position);

        intent.putExtra("id", selected.get("id"));

        startActivity(intent);
    }
}

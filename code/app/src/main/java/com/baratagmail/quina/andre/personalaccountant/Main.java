package com.baratagmail.quina.andre.personalaccountant;


import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.baratagmail.quina.andre.personalaccountant.components.FormPair;
import com.baratagmail.quina.andre.personalaccountant.components.MarkedListAdaptor;
import com.baratagmail.quina.andre.personalaccountant.database.DBManager;

import java.io.File;
import java.util.Arrays;


public class Main extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener {
    private final static int CATEGORY_EDIT = 0;
    private final static int CATEGORY_DELETE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_categories);

        Button addReceipt = (Button)findViewById(R.id.add_receipt);
        Button addCategory = (Button)findViewById(R.id.add_category);
        ListView categories = (ListView)findViewById(R.id.categories);

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
    }

    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)
                item.getMenuInfo();
        final int index = info.position;
        final ListView list = (ListView)findViewById(R.id.categories);
        final String id = ((FormPair)list.getItemAtPosition(index)).get("id");

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
                                    deleteCategory(id);
                                }
                            })
                    .setNegativeButton("No", null).show();
        }
        return true;
    }

    private void deleteCategory(String id) {
        DBManager db = new DBManager(getBaseContext());
        Cursor cursor;

        db.open();

        cursor = db.select("Receipt", new String[]{"image_path"},
                "category_id = ?",
                Arrays.asList(id)
        );

        while (!cursor.isAfterLast()) {
            File image = new File(cursor.getString(0));

            image.delete();

            cursor.moveToNext();
        }

        db.delete("Receipt",
            "category_id = ?",
            Arrays.asList(id)
        );

        db.delete("Category",
                "id = ?",
                Arrays.asList(id)
        );

        cursor.close();
        db.close();
        setCategoryList();
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

        cursor = db.select("Select c.id, c.name, c.budget, sum(r.cost) as total "
                + "from Category c "
                + "left join Receipt r on c.id = r.category_id "
                + "and r.date_recorded >= c.last_reset "
                + "group by c.id"
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
                            "label", cursor.getString(1),
                            "budget", ("€" + cursor.getFloat(2)),
                            "usage",  ("€" + cursor.getFloat(3) + " spent")
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
            intent = new Intent(this, Receipt.class);
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

package com.baratagmail.quina.andre.personalaccountant;


import android.content.Intent;
import android.database.Cursor;
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


public class Main extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener {
    private final static int CATEGORY_EDIT = 0;

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
    }

    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)
                item.getMenuInfo();
        int index = info.position;
        ListView list = (ListView)findViewById(R.id.categories);

        if (item.getItemId() == CATEGORY_EDIT) {
            Intent intent = new Intent(this, CategoryForm.class);

            intent.putExtra("id",
                    ((FormPair)list.getItemAtPosition(index)).get("id")
            );

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

        cursor = db.select("Select c.id, c.name, c.budget, sum(r.cost) as total "
                + "from Category c "
                + "left join Receipt r on c.id = r.category_id "
                + "and r.date_recorded >= c.last_reset "
                + "group by c.id"
        );

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

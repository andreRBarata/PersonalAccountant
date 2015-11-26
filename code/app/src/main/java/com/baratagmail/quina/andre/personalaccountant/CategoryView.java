package com.baratagmail.quina.andre.personalaccountant;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextMenu;
import android.view.GestureDetector;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.baratagmail.quina.andre.personalaccountant.components.FormPair;
import com.baratagmail.quina.andre.personalaccountant.database.DBManager;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by andre on 21-11-2015.
 *
 * Displays a cycle and its receipts for a category
 */
public class CategoryView extends AppCompatActivity
        implements GestureDetector.OnGestureListener,
        ListView.OnItemClickListener {
    private GestureDetectorCompat mDetector;
    private final static int CATEGORY_DELETE = 1;
    private final int SWIPE_TOLERANCE = 50;
    private String id;
    private int page;
    private boolean canContinue;
    private boolean outsider;

    private TextView budget;
    private TextView spent;
    private TextView tally;
    private ImageView image;
    private ListView receipts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.category_view);
        Intent intent = getIntent();

        budget = (TextView) findViewById(R.id.category_budget);
        spent = (TextView) findViewById(R.id.category_spent);
        tally = (TextView) findViewById(R.id.category_tally);
        image = (ImageView) findViewById(R.id.category_image);
        receipts = (ListView) findViewById(R.id.receipts);


        receipts.setOnItemClickListener(this);

        mDetector = new GestureDetectorCompat(this,this);

        //Page parameters
        id = intent.getStringExtra("id");
        page = intent.getIntExtra("page", 0);
        outsider = intent.getBooleanExtra("outsider", false);

        setReceiptsList();

        registerForContextMenu(receipts);

    }

    public void setReceiptsList() {
        DBManager db = new DBManager(getBaseContext());
        ArrayList<FormPair> receiptsList =
                new ArrayList<FormPair>();
        Cursor categoryCursor;
        Cursor receiptsCursor;
        String startDate;
        String endDate;

        db.open();

        categoryCursor = db.select(
                "SpendingHistory_xp",
                new String[]{
                        "name",
                        "total",
                        "budget",
                        "image_path",
                        "start_date",
                        "end_date"
                },
                "category_id = ?",
                Arrays.asList(id)
        );

        setTitle(categoryCursor.getString(0));

        categoryCursor.moveToPosition(page);

        //Checks if there are older receipts
        canContinue = !categoryCursor.isLast();

        startDate = categoryCursor.getString(4);
        endDate = categoryCursor.getString(5);

        budget.setText(
                "€" + String.valueOf(categoryCursor.getFloat(2)) + " budget"
        );
        spent.setText(
                "€" + String.valueOf(categoryCursor.getFloat(1)) + " spent"
        );


        image.setImageResource(
                getResources().getIdentifier(
                        categoryCursor.getString(3),
                        "drawable",
                        getPackageName()
                )
        );

        //If there are receipts
        if (startDate != null && endDate != null) {
            tally.setText(
                    startDate.split(" ")[0] + " to " + endDate.split(" ")[0]
            );

            receiptsCursor = db.select(
                    "select id, date_recorded, cost, image_path from Receipt "
                            + "where date_recorded between ? and ?",
                    Arrays.asList(startDate, endDate)
            );

            while (!receiptsCursor.isAfterLast()) {
                receiptsList.add(
                        new FormPair(
                                "id", String.valueOf(receiptsCursor.getInt(0)),
                                "text1", receiptsCursor.getString(1),
                                "text2", "€" + receiptsCursor.getString(2),
                                "photo", receiptsCursor.getString(3)
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

        receipts.setAdapter(
                new SimpleAdapter(
                        receipts.getContext(),
                        receiptsList,
                        R.layout.receipt_row,
                        new String[]{"text1", "text2"},
                        new int[]{R.id.text1, R.id.text2}
                )
        );
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, CATEGORY_DELETE, 0, R.string.delete);
    }

    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)
                item.getMenuInfo();
        final int index = info.position;
        final String id = ((FormPair)receipts.getItemAtPosition(index)).get("id");

        if (item.getItemId() == CATEGORY_DELETE) {
            new AlertDialog.Builder(this)
                    .setMessage(R.string.category_deletemsg)
                    .setPositiveButton(R.string.yes,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    DBManager db = new DBManager(getBaseContext());
                                    db.open();
                                    db.deleteReceipt(id);
                                    setReceiptsList();
                                    db.close();
                                }
                            })
                    .setNegativeButton(R.string.no, null).show();
        }
        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        this.mDetector.onTouchEvent(event);
        // Be sure to call the superclass implementation
        return super.onTouchEvent(event);
    }

    //Reacts to swipe
    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        if (!outsider) {
            if (e2.getRawX() - e1.getRawX() > SWIPE_TOLERANCE) { //Swipe left
                if (page != 0) {
                    finish();
                } else {
                    Toast.makeText(this, R.string.nomore_recent, Toast.LENGTH_SHORT).show();
                }
            } else if (e1.getRawX() - e2.getRawX() > SWIPE_TOLERANCE) { //Swipe right
                if (canContinue) {
                    Intent intent = new Intent(this, CategoryView.class);

                    intent.putExtra("id", id);
                    intent.putExtra("page", page + 1);

                    startActivity(intent);
                } else {
                    Toast.makeText(this, R.string.noolder, Toast.LENGTH_SHORT).show();
                }
            }
        }
        return false;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String photo_path = ((FormPair) parent.getItemAtPosition(position)).get("photo");

        if (photo_path != null) {
            Intent intent = new Intent(
                    Intent.ACTION_VIEW,
                    android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI
            );

            intent.setDataAndType(Uri.fromFile(
                        new File(photo_path)
                    ),
                    "image/*"
            );
            startActivity(intent);
        }
        else {
            Toast.makeText(this,R.string.nophoto,Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }
}

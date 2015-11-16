package com.baratagmail.quina.andre.personalaccountant;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import com.baratagmail.quina.andre.personalaccountant.components.FormPair;
import com.baratagmail.quina.andre.personalaccountant.database.DBManager;

import java.io.File;
import java.util.TreeMap;

/**
 * Created by andre on 15-11-2015.
 */
public class Receipt extends AppCompatActivity implements View.OnClickListener {
    static final int REQUEST_IMAGE_CAPTURE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.receipt);

        Spinner category = (Spinner)findViewById(R.id.category);

        ImageView receipt = (ImageView)findViewById(R.id.receipt);
        receipt.setOnClickListener(this);

        //Adding categories to the list
        ArrayAdapter<FormPair> categories =
                new ArrayAdapter<FormPair>(
                        category.getContext(),
                        android.R.layout.simple_spinner_dropdown_item
                );
        DBManager db = new DBManager(getBaseContext());
        Cursor cursor;

        db.open();

        cursor = db.select("Category", new String[] {"id","name"});

        while (!cursor.isAfterLast()) {
            categories.add(
                    new FormPair(
                            "value", String.valueOf(cursor.getInt(0)),
                            "name", cursor.getString(1)
                    )
            );

            cursor.moveToNext();
        }

        category.setAdapter(categories);

        db.close();

    }

    public void onClick(View v) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File file = new File(Environment.getExternalStorageDirectory(), "aname.jpg");

        intent.putExtra(MediaStore.EXTRA_OUTPUT, file); // set the image file name
        // start the image capture Intent

        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        final ImageView receipt = (ImageView)findViewById(R.id.receipt);

        receipt.setImageBitmap(BitmapFactory.decodeFile(Environment.getExternalStorageDirectory() + "/" + "aname.jpg"));
        Log.d("log", "log");
    }
}

package com.baratagmail.quina.andre.personalaccountant;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.baratagmail.quina.andre.personalaccountant.components.FormPair;
import com.baratagmail.quina.andre.personalaccountant.database.DBManager;

import java.io.File;
import java.util.TreeMap;

/**
 * Created by andre on 15-11-2015.
 */
public class Receipt extends AppCompatActivity implements View.OnClickListener {
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private Bitmap photo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.receipt);

        File cache = new File(getExternalFilesDir(null) + File.separator + "cache");
        cache.mkdir();

        Spinner category = (Spinner)findViewById(R.id.category);

        ImageView receipt = (ImageView)findViewById(R.id.receipt_photo);
        Button save = (Button) findViewById(R.id.receipt_save);
        receipt.setOnClickListener(this);
        save.setOnClickListener(this);

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
        if (v.getId() == R.id.receipt_photo) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            File file = new File(
                    getExternalFilesDir(null) + File.separator + "cache",
                    "temp.jpg"
            );


            file.delete();

            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file)); // set the image file name


            // start the image capture Intent
            startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
        }
        else if (v.getId() == R.id.receipt_save) {
            Toast.makeText(this,"save", Toast.LENGTH_SHORT).show();
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_CAPTURE) {
            final ImageView receipt = (ImageView) findViewById(R.id.receipt_photo);
            String path = getExternalFilesDir(null) + File.separator + "cache"
                    + File.separator + "temp.jpg";

            if (resultCode == RESULT_OK) {
                photo = BitmapFactory.decodeFile(
                    path
                );


                receipt.setImageBitmap(
                        photo
                );
            }
        }
    }
}

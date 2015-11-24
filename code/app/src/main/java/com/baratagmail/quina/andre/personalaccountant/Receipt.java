package com.baratagmail.quina.andre.personalaccountant;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.baratagmail.quina.andre.personalaccountant.components.FormPair;
import com.baratagmail.quina.andre.personalaccountant.database.DBManager;
import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

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

        //Make cache dir
        File cache = new File(getExternalFilesDir(null) + File.separator + "cache");
        cache.mkdir();

        Spinner category = (Spinner)findViewById(R.id.category);

        //Adding listeners
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
                            "id", String.valueOf(cursor.getInt(0)),
                            "name", cursor.getString(1)
                    )
            );

            cursor.moveToNext();
        }

        category.setAdapter(categories);
        cursor.close();
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
            Calendar cal = Calendar.getInstance();
            //Sqlite's date format
            DBManager db = new DBManager(getBaseContext());
            Cursor cursor;
            Date last_reset;
            Date next_reset;
            boolean newCycle = false;


            File file = new File(
                    getExternalFilesDir(null) + File.separator + "receipts",
                    (System.currentTimeMillis()) + ".jpg"
            );
            Spinner category = (Spinner)findViewById(R.id.category);
            EditText cost = (EditText) findViewById(R.id.cost);

            String category_id = ((FormPair)
                            category.getSelectedItem()).get("id");


            ContentValues queryValues = new ContentValues();


            //Saving photo
            try {
                OutputStream fOut = new FileOutputStream(file);
                photo.compress(Bitmap.CompressFormat.JPEG, 85, fOut);

                fOut.flush();
                fOut.close();
            } catch (Exception e) {
                Toast.makeText(this,
                        R.string.sdcard_error,
                        Toast.LENGTH_SHORT
                ).show();
            }

            db.open();

            queryValues.put("image_path", file.getPath());
            queryValues.put("category_id", category_id);
            queryValues.put("cost", cost.getText().toString());

            //Saving Receipt
            db.insert("Receipt", queryValues);

            cursor = db.select("Category", new String[]{"last_reset","counting_period"},
                    "id = ?",
                    Arrays.asList(category_id)
            );

            try {
                newCycle = db.updateCategory(category_id);
            }
            catch (ParseException e) {
                Toast.makeText(this, "Internal Error", Toast.LENGTH_LONG);
            }

            if (cursor.isNull(0) || newCycle) {
                ContentValues values = new ContentValues();
                int counting_period = cursor.getInt(1);

                //noinspection ResourceType
                cal.set(counting_period,
                        Calendar.getInstance().getActualMinimum(counting_period)
                );

                last_reset = cal.getTime();

                //noinspection ResourceType
                cal.set(counting_period,
                        Calendar.getInstance().getActualMaximum(counting_period)
                );

                next_reset = cal.getTime();

                values.put("last_reset",
                        db.dateFormat.format(last_reset)
                );
                values.put("next_reset",
                        db.dateFormat.format(next_reset)
                );


                db.update(
                        "Category",
                        values,
                        "id = ?",
                        Arrays.asList(category_id)
                );
            }

            cursor.close();
            db.close();



            Toast.makeText(this,"saved", Toast.LENGTH_SHORT).show();

            finish();
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_CAPTURE) {
            final ImageView receipt = (ImageView) findViewById(R.id.receipt_photo);
            //TessBaseAPI tessBaseAPI = new TessBaseAPI();
            String path = getExternalFilesDir(null) + File.separator + "cache"
                    + File.separator + "temp.jpg";

            if (resultCode == RESULT_OK) {
                photo = BitmapFactory.decodeFile(
                    path
                );

                //tessBaseAPI.setImage(photo);
                //Log.d("photo",tessBaseAPI.getUTF8Text());

                receipt.setImageBitmap(
                        photo
                );
            }
        }
    }
}

package com.baratagmail.quina.andre.personalaccountant;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.baratagmail.quina.andre.personalaccountant.components.FormPair;
import com.baratagmail.quina.andre.personalaccountant.database.DBManager;

import java.io.File;
import java.util.TreeMap;

/**
 * Created by andre on 15-11-2015.
 */
public class Receipt extends AppCompatActivity implements View.OnClickListener {
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private File receiptsDir;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.receipt);

        receiptsDir = new File(
                getExternalFilesDir(null) + File.separator + "receipts"
        );

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
        TextView pathView = (TextView) findViewById(R.id.receipt_path);


        File file = new File(
                receiptsDir,
                (System.currentTimeMillis()) + ".jpg"
        );
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file)); // set the image file name

        if (pathView.getText() != "") {
            Log.d("log","teste");
            File old = new File(pathView.getText().toString());
            old.delete();
        }

        pathView.setText(file.getAbsolutePath()); //Add file path to "Form"


        // start the image capture Intent
        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_CAPTURE) {
            final ImageView receipt = (ImageView) findViewById(R.id.receipt);
            TextView pathView = (TextView) findViewById(R.id.receipt_path);

            if (resultCode == RESULT_OK) {

                receipt.setImageBitmap(BitmapFactory.decodeFile(
                                pathView.getText().toString()
                        )
                );
            }
            else {
                pathView.setText("");
            }
        }
    }
}

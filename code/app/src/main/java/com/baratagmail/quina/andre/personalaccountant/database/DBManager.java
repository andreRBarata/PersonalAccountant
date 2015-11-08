package com.baratagmail.quina.andre.personalaccountant.database;

/**
 * Created by andre on 06-11-2015.
 */
import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.baratagmail.quina.andre.personalaccountant.R;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Scanner;


/**
 * Created by andre on 29-10-2015.
 */
public class DBManager {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "PersonalAccountant";

    private final Context context;
    private final MyDatabaseHelper DBHelper;
    private SQLiteDatabase db;

    public DBManager(Context ctx) {
        this.context = ctx;
        DBHelper = new MyDatabaseHelper(context);
    }

    public static class MyDatabaseHelper extends SQLiteOpenHelper {
        private Resources resources;

        public MyDatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);

            resources = context.getResources();
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            Scanner scan = new Scanner(resources.openRawResource(R.raw.database));
            StringBuilder commands = new StringBuilder();

            while (scan.hasNext()) {
                String line = scan.nextLine();
                commands.append(line + "\n");
                if (line.endsWith(");")) {
                    db.execSQL(commands.toString());
                    commands = new StringBuilder();
                }
            }

        }

        /*public Cursor select(String table, String[] columns, Map parameters) {
            db.query(table, columns )
        }*/

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }

    public DBManager open() throws SQLException {
        db = DBHelper.getWritableDatabase();

        return this;
    }

    public void close() {
        DBHelper.close();
    }

}

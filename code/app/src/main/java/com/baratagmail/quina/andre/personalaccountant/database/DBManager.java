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
import android.widget.Toast;

import com.baratagmail.quina.andre.personalaccountant.R;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Scanner;


/**
 * Created by andre on 29-10-2015.
 *
 * Database connector
 *
 * Creates and gives access to the app database
 */

public class DBManager {
    public static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
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

    /*
    * Select table query
    * params cheat sheet:
    * params[0] - where clause
    * params[1] - where parameters (string seperated by comas)
    * params[2] - group by
    * params[3] - having
    * params[4] - order by
    */
    public Cursor select(String table, String[] columns, Object... params) {

        Cursor cursor = db.query(table, columns,
                (params.length > 0)? (String)params[0]: null,
                (params.length > 1)? (String[])((List)params[1]).toArray(): null,
                (params.length > 2)? (String)params[2]: null,
                (params.length > 3)? (String)params[3]: null,
                (params.length > 4)? (String)params[4]: null
        );

        cursor.moveToFirst();

        return cursor;
    }

    public Cursor select(String query) {
        Cursor cursor = db.rawQuery(query, null);

        cursor.moveToFirst();

        return cursor;
    }

    public Cursor select(String query, List<String> params) {
        Cursor cursor = db.rawQuery(query, (String[])params.toArray());

        cursor.moveToFirst();

        return cursor;
    }

    public long insert(String table, ContentValues values) {
        return db.insert(table, null, values);
    }

    public int update(String table, ContentValues values, String where, List<String> params) {
        return db.update(table, values, where, (String[]) params.toArray());
    }

    public int delete(String table, String where, List<String> params) {
        return db.delete(table, where, (String[]) params.toArray());
    }


    public void deleteCategory(String id) {
        Cursor cursor;

        cursor = select("Receipt", new String[]{"image_path"},
                "category_id = ?",
                Arrays.asList(id)
        );

        while (!cursor.isAfterLast()) {
            File image = new File(cursor.getString(0));

            image.delete();

            cursor.moveToNext();
        }

        delete("Receipt",
                "category_id = ?",
                Arrays.asList(id)
        );

        delete("Category",
                "id = ?",
                Arrays.asList(id)
        );

        cursor.close();
    }

    public boolean updateCategory(String id) throws ParseException{
        Date last_reset;
        Date next_reset;
        boolean toReturn = false;

        Cursor cursor = select("Category", new String[]{"last_reset", "next_reset", "counting_period"},
                "id = ?",
                Arrays.asList(id)
        );

        last_reset = dateFormat.parse(cursor.getString(0));
        next_reset = dateFormat.parse(cursor.getString(1));

        if (new Date().after(next_reset)) {
            ContentValues values = new ContentValues();

            values.put("start_date", dateFormat.format(last_reset));
            values.put("end_date", dateFormat.format(next_reset));

            toReturn = true;

            insert(
                    "SpendingHistory",
                    values
            );
        }

        return toReturn;
    }
}

package com.scripton.in.test.SQLiteDatabase;

/**
 * Created by Nitin on 10/18/2015.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class notifications {
    public static String r = "";
    public static final String KEY_DATE = "id";
    public static final String KEY_TEXT = "text";
    private static final String DATABASE_NAME = "notification";
    private static final String DATABASE_TABLE = "notification_table";
    private static final int DATABASE_VERSION = 1;

    private DbHelpers ourHelper;
    private final Context ourContext;
    private SQLiteDatabase ourDataBase;

    private static class DbHelpers extends SQLiteOpenHelper {

        public DbHelpers(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {

            db.execSQL("CREATE TABLE " + DATABASE_TABLE + " (" + KEY_DATE
                    + " TEXT NOT NULL , " + KEY_TEXT
                    + " TEXT NOT NULL );"

            );
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {
            db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
            onCreate(db);

        }

    }

    public notifications(Context c) {
        ourContext = c;

    }

    public notifications open() throws SQLException {

        ourHelper = new DbHelpers(ourContext);

        ourDataBase = ourHelper.getWritableDatabase();

        return this;
    }

    public void close() {
        ourHelper.close();
    }

    public long createEntry(String text, String date) {

        ContentValues cv = new ContentValues();
        cv.put(KEY_TEXT, text);
        cv.put(KEY_DATE, date);
        return ourDataBase.insert(DATABASE_TABLE, null, cv);

    }

    public JSONObject getNotifications() {
        JSONObject notiObj;
        JSONArray notiArray = new JSONArray();

        String[] column = new String[]{KEY_DATE, KEY_TEXT};
        Cursor c = ourDataBase.query(DATABASE_TABLE, column, null, null, null,
                null, null);

        int date = c.getColumnIndex(KEY_DATE);
        int text = c.getColumnIndex(KEY_TEXT);
        int i=0;
        for (c.moveToFirst(), i = 0; !c.isAfterLast(); c.moveToNext(), i++) {
            try {
                notiObj = new JSONObject();

                notiObj.put("date", c.getString(date));
                notiObj.put("text", c.getString(text));

                notiArray.put(notiObj);

                notiObj = null;

            } catch (Exception e) {

            }
        }

        JSONObject videoJSON = new JSONObject();
        try {
            videoJSON.put("Notifications", notiArray);
        } catch (JSONException e) {
        }

        return videoJSON;
    }
    public void delete(String text) {
        ourDataBase.delete(DATABASE_TABLE, KEY_TEXT + "=" + text, null);

    }

    public void erase(String id) {
        ourDataBase.execSQL("DROP TABLE IF EXISTS contacts");
        //onCreate(ourDataBase);
    }

}


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

public class galleryList {
    public static String r = "";
    public static final String KEY_ID = "id";
    public static final String KEY_NAME = "name";
    private static final String DATABASE_NAME = "galleryList";
    private static final String DATABASE_TABLE = "galleryList_table";
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

            db.execSQL("CREATE TABLE " + DATABASE_TABLE + " (" + KEY_ID
                    + " TEXT NOT NULL , " + KEY_NAME
                    + " TEXT NOT NULL );"

            );
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {
            db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
            onCreate(db);

        }

    }

    public galleryList(Context c) {
        ourContext = c;

    }

    public galleryList open() throws SQLException {

        ourHelper = new DbHelpers(ourContext);

        ourDataBase = ourHelper.getWritableDatabase();

        return this;
    }

    public void close() {
        ourHelper.close();
    }

    public long createEntry(String id, String name) {

        ContentValues cv = new ContentValues();
        cv.put(KEY_ID, id);
        cv.put(KEY_NAME, name);
        return ourDataBase.insert(DATABASE_TABLE, null, cv);

    }

    public JSONObject getGalleryList() {
        JSONObject galleryObj;
        JSONArray galleryArray = new JSONArray();

        String[] column = new String[]{KEY_ID, KEY_NAME};
        Cursor c = ourDataBase.query(DATABASE_TABLE, column, null, null, null,
                null, null);

        int id = c.getColumnIndex(KEY_ID);
        int txt = c.getColumnIndex(KEY_NAME);
        int i=0;
        for (c.moveToFirst(), i = 0; !c.isAfterLast(); c.moveToNext(), i++) {
            try {
                galleryObj = new JSONObject();

                galleryObj.put("id", c.getString(id));
                galleryObj.put("name", c.getString(txt));

                galleryArray.put(galleryObj);

                galleryObj = null;

            } catch (Exception e) {

            }
        }

        JSONObject galleryJSON = new JSONObject();
        try {
            galleryJSON.put("GalleryList", galleryArray);
        } catch (JSONException e) {
        }

        return galleryJSON;
    }
    public void delete(String id) {
        ourDataBase.delete(DATABASE_TABLE, KEY_ID + "=" + id, null);

    }

    public void erase(String id) {
        ourDataBase.execSQL("DROP TABLE IF EXISTS contacts");
        //onCreate(ourDataBase);
    }

}


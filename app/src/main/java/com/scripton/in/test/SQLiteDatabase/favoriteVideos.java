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
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class favoriteVideos {
    public static String r = "";
    public static final String KEY_IMAGE = "image";
    public static final String KEY_TEXT = "text";
    public static final String KEY_VIDEOLINK = "video_link";
    public static final String KEY_CHANNELID = "channel_id";
    private static final String DATABASE_NAME = "favoriteVideo";
    private static final String DATABASE_TABLE = "favoriteVideo_table";
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

            db.execSQL("CREATE TABLE " + DATABASE_TABLE + " (" + KEY_TEXT
                    + " TEXT NOT NULL , " + KEY_IMAGE
                    + " TEXT NOT NULL , " + KEY_VIDEOLINK
                    + " TEXT NOT NULL , " + KEY_CHANNELID
                    + " TEXT NOT NULL );"

            );
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {
            db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
            onCreate(db);

        }

    }

    public favoriteVideos(Context c) {
        ourContext = c;

    }

    public favoriteVideos open() throws SQLException {

        ourHelper = new DbHelpers(ourContext);

        ourDataBase = ourHelper.getWritableDatabase();

        return this;
    }

    public void close() {
        ourHelper.close();
    }

    public long createEntry(String text, String image, String videoLink, int channelID) {

        ContentValues cv = new ContentValues();
        cv.put(KEY_TEXT, text);
        cv.put(KEY_IMAGE, image);
        cv.put(KEY_VIDEOLINK, videoLink);
        cv.put(KEY_CHANNELID, channelID+"");
        return ourDataBase.insert(DATABASE_TABLE, null, cv);

    }

    public JSONObject getFavoriteVideos() {
        JSONObject favObj;
        JSONArray favArray = new JSONArray();

        String[] column = new String[]{KEY_TEXT, KEY_IMAGE,KEY_VIDEOLINK,KEY_CHANNELID};
        Cursor c = ourDataBase.query(DATABASE_TABLE, column, null, null, null,
                null, null);

        int text = c.getColumnIndex(KEY_TEXT);
        int image = c.getColumnIndex(KEY_IMAGE);
        int videoLink = c.getColumnIndex(KEY_VIDEOLINK);
        int channelID = c.getColumnIndex(KEY_CHANNELID);
        int i=0;
        for (c.moveToFirst(), i = 0; !c.isAfterLast(); c.moveToNext(), i++) {
            try {
                favObj = new JSONObject();

                favObj.put("name", c.getString(text));
                favObj.put("image", c.getString(image));
                favObj.put("videoLink", c.getString(videoLink));
                favObj.put("channelID", c.getString(channelID));

                favArray.put(favObj);

                favObj = null;

            } catch (Exception e) {
                Toast.makeText(ourContext,e.getMessage(),Toast.LENGTH_SHORT).show();

            }
        }

        JSONObject videoJSON = new JSONObject();
        try {
            videoJSON.put("FavoriteVideos", favArray);
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


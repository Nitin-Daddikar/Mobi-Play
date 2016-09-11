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

public class galleryVideos {
    public static String r = "";
    public static final String KEY_ID = "id";
    public static final String KEY_NAME = "name";
    public static final String KEY_IMAGE = "image";
    public static final String KEY_VIDEO_LINK = "video_link";
    public static final String KEY_CHANNEL_ID = "channel_id";
    private static final String DATABASE_NAME = "galleryVideos";
    private static final String DATABASE_TABLE = "galleryVideos_table";
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
                    + " TEXT NOT NULL , " + KEY_IMAGE
                    + " TEXT NOT NULL , " + KEY_VIDEO_LINK
                    + " TEXT NOT NULL , " + KEY_CHANNEL_ID
                    + " TEXT NOT NULL );"

            );
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {
            db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
            onCreate(db);

        }

    }

    public galleryVideos(Context c) {
        ourContext = c;

    }

    public galleryVideos open() throws SQLException {

        ourHelper = new DbHelpers(ourContext);

        ourDataBase = ourHelper.getWritableDatabase();

        return this;
    }

    public void close() {
        ourHelper.close();
    }

    public long createEntry(String id, String name,String image,String video_link,String channel_id) {

        ContentValues cv = new ContentValues();
        cv.put(KEY_ID, id);
        cv.put(KEY_NAME, name);
        cv.put(KEY_IMAGE, image);
        cv.put(KEY_VIDEO_LINK, video_link);
        cv.put(KEY_CHANNEL_ID, channel_id);
        return ourDataBase.insert(DATABASE_TABLE, null, cv);

    }

    public JSONObject getGalleryVideos() {
        JSONObject videoObj;
        JSONArray videoArray = new JSONArray();

        String[] column = new String[]{KEY_ID, KEY_NAME, KEY_IMAGE, KEY_VIDEO_LINK, KEY_CHANNEL_ID};
        Cursor c = ourDataBase.query(DATABASE_TABLE, column, null, null, null,
                null, null);

        int id = c.getColumnIndex(KEY_ID);
        int name = c.getColumnIndex(KEY_NAME);
        int image = c.getColumnIndex(KEY_IMAGE);
        int link = c.getColumnIndex(KEY_VIDEO_LINK);
        int channel_id = c.getColumnIndex(KEY_CHANNEL_ID);
        int i=0;
        for (c.moveToFirst(), i = 0; !c.isAfterLast(); c.moveToNext(), i++) {
            try {
                videoObj = new JSONObject();

                videoObj.put("id", c.getString(id));
                videoObj.put("name", c.getString(name));
                videoObj.put("image", c.getString(image));
                videoObj.put("video_link", c.getString(link));
                videoObj.put("channel_id", c.getString(channel_id));

                videoArray.put(videoObj);

                videoObj = null;

            } catch (Exception e) {

            }
        }

        JSONObject videoJSON = new JSONObject();
        try {
            videoJSON.put("GalleryVideos", videoArray);
        } catch (JSONException e) {
        }

        return videoJSON;
    }
    public void delete(String id) {
        ourDataBase.delete(DATABASE_TABLE, KEY_ID + "=" + id, null);

    }

    public void erase(String id) {
        ourDataBase.execSQL("DROP TABLE IF EXISTS contacts");
        //onCreate(ourDataBase);
    }

}


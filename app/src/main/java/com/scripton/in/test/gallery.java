package com.scripton.in.test;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.scripton.in.test.ListAdapter;
import com.scripton.in.test.SQLiteDatabase.favoriteVideos;
import com.scripton.in.test.SQLiteDatabase.galleryList;
import com.scripton.in.test.SQLiteDatabase.galleryVideos;
import com.scripton.in.test.test.VideoPlayerActivity;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by Nitin on 5/24/2016.
 */
public class gallery extends AppCompatActivity {
    ArrayList<Integer> channelID = new ArrayList<>();
    ArrayList<String> channelName = new ArrayList<>();

    ArrayList<String> VideoName = new ArrayList<>();
    ArrayList<String> VideoImage = new ArrayList<>();
    ArrayList<String> VideoLink = new ArrayList<>();
    ArrayList<Integer> VideoChannelID = new ArrayList<>();

    ArrayList<String> favVideoName = new ArrayList<>();
    ArrayList<String> favVideoImage = new ArrayList<>();
    ArrayList<String> favVideoLink = new ArrayList<>();
    ArrayList<Integer> favVideoChannelID = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gallery);
        getSupportActionBar().hide();
        final FloatingActionButton fab_back = (FloatingActionButton) findViewById(R.id.fab_back);
        fab_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        hideStatuBar();

        galleryList gl = new galleryList(this);
        gl.open();
        JSONObject galleryListObj = gl.getGalleryList();
        gl.close();
        System.gc();

        try {
            JSONArray jArray = galleryListObj.getJSONArray("GalleryList");
            for (int i = 0; i < jArray.length(); i++) {
                JSONObject json_data = jArray.getJSONObject(i);
                channelID.add(Integer.parseInt(json_data.getString("id").trim()));
                channelName.add(json_data.getString("name"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        galleryVideos gv = new galleryVideos(this);
        gv.open();
        JSONObject galleryVideosObj = gv.getGalleryVideos();
        gv.close();
        System.gc();

        try {
            JSONArray jArray = galleryVideosObj.getJSONArray("GalleryVideos");
            for (int i = 0; i < jArray.length(); i++) {
                JSONObject json_data = jArray.getJSONObject(i);
                VideoName.add(json_data.getString("name"));
                VideoImage.add(json_data.getString("image"));
                VideoLink.add(json_data.getString("video_link"));
                VideoChannelID.add(Integer.parseInt(json_data.getString("channel_id").trim()));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


        favoriteVideos fav = new favoriteVideos(this);
        fav.open();
        JSONObject favObj = fav.getFavoriteVideos();
        fav.close();
        System.gc();

        try {
            JSONArray favArray = favObj.getJSONArray("FavoriteVideos");
            for (int i = 0; i < favArray.length(); i++) {
                JSONObject fav_data = favArray.getJSONObject(i);
                favVideoName.add(fav_data.getString("name"));
                favVideoImage.add(fav_data.getString("image"));
                favVideoLink.add(fav_data.getString("videoLink"));
                favVideoChannelID.add(Integer.parseInt(fav_data.getString("channelID").trim()));
            }
        } catch (JSONException e) {
            Toast.makeText(this,e.getMessage(),Toast.LENGTH_SHORT).show();
        }


        addAtList1();
        addAtList2();
        addAtList3();
        addAtList4();
    }

    void addAtList1() {
//        int pic[] = {R.mipmap.list_21, R.mipmap.list_22, R.mipmap.list_23, R.mipmap.list_24, R.mipmap.list_25, R.mipmap.list_26, R.mipmap.list_27, R.mipmap.list_28, R.mipmap.list_29};
//        String txt[] = {"A", "B", "C", "D", "E", "F", "G", "H", "I"};
//        for (int i = 0; i < 9; i++) {
//            LinearLayout myLayout = (LinearLayout) findViewById(R.id.list1);
//            View hiddenInfo = getLayoutInflater().inflate(R.layout.child_horizontal_lv, myLayout, false);
//
//            //Get References to the ImageView
//            ImageView iv = (ImageView) hiddenInfo.findViewById(R.id.iv);
//            final TextView tv = (TextView) hiddenInfo.findViewById(R.id.tv);
//            // Update the ImageView
//            iv.setImageResource(pic[i]);
//            tv.setText(txt[i]);
//            iv.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    if (new check_internet().check(gallery.this)) {
//                        Intent intt = new Intent(gallery.this, VideoPlayerActivity.class);
//                        intt.putExtra("videoLink", "http://scripton.in/mobiplay/android_connect/videos/1.3gp");
//                        startActivity(intt);
//                        finish();
//                    } else {
//                        show_internet_popup();
//                    }
//                    //Toast.makeText(gallery.this, tv.getText().toString().trim(),Toast.LENGTH_SHORT).show();
//                }
//            });
//            myLayout.addView(hiddenInfo);
//        }

        for (int i = 0; i < favVideoLink.size(); i++) {
            LinearLayout myLayout = (LinearLayout) findViewById(R.id.list1);
            View hiddenInfo = getLayoutInflater().inflate(R.layout.child_horizontal_lv, myLayout, false);

            //Get References to the ImageView
            ImageView iv = (ImageView) hiddenInfo.findViewById(R.id.iv);
            final TextView tv = (TextView) hiddenInfo.findViewById(R.id.tv);
            // Update the ImageView

            Picasso.with(this)
                    .load(favVideoImage.get(i))
                    .placeholder(R.mipmap.list_2) // optional
                    .error(R.mipmap.list_3).memoryPolicy(MemoryPolicy.NO_CACHE)            // optional
                    .into(iv);

            //iv.setImageResource(pic[i]);
            tv.setText(favVideoName.get(i));
            final int place = i;
            iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (new check_internet().check(gallery.this)) {
                        Intent intt = new Intent(gallery.this, VideoPlayerActivity.class);
                        intt.putExtra("videoLink", favVideoLink.get(place));
                        intt.putExtra("videoName", favVideoName.get(place));
                        intt.putExtra("videoImage", favVideoImage.get(place));
                        intt.putExtra("videoChannelID", favVideoChannelID.get(place));
                        startActivity(intt);
                        finish();
                    } else {
                        show_internet_popup();
                    }
                    //Toast.makeText(gallery.this, tv.getText().toString().trim(),Toast.LENGTH_SHORT).show();
                }
            });
            myLayout.addView(hiddenInfo);
        }

    }

    void addAtList2() {
        final ArrayList<String> pic = new ArrayList<>();
        final ArrayList<String> text = new ArrayList<>();
        final ArrayList<String> vid_link = new ArrayList<>();
        final ArrayList<Integer> channel_id = new ArrayList<>();

        for (int i = 0; i < VideoChannelID.size(); i++) {
            if (VideoChannelID.get(i) == 1) {
                pic.add(VideoImage.get(i));
                text.add(VideoName.get(i));
                vid_link.add(VideoLink.get(i));
                channel_id.add(VideoChannelID.get(i));
            }
        }

        //int pic[] = {R.mipmap.list_1, R.mipmap.list_2, R.mipmap.list_3, R.mipmap.list_4};
        //String txt[] = {"A", "B", "C", "D"};
        for (int i = 0; i < pic.size(); i++) {
            LinearLayout myLayout = (LinearLayout) findViewById(R.id.list2);
            View hiddenInfo = getLayoutInflater().inflate(R.layout.child_horizontal_lv, myLayout, false);

            //Get References to the ImageView
            ImageView iv = (ImageView) hiddenInfo.findViewById(R.id.iv);
            final TextView tv = (TextView) hiddenInfo.findViewById(R.id.tv);
            // Update the ImageView

            Picasso.with(this)
                    .load(pic.get(i))
                    .placeholder(R.mipmap.list_2) // optional
                    .error(R.mipmap.list_3).memoryPolicy(MemoryPolicy.NO_CACHE)            // optional
                    .into(iv);

            //iv.setImageResource(pic[i]);
            tv.setText(text.get(i));
            final int place = i;
            iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (new check_internet().check(gallery.this)) {
                        Intent intt = new Intent(gallery.this, VideoPlayerActivity.class);
                        intt.putExtra("videoLink", vid_link.get(place));
                        intt.putExtra("videoName", text.get(place));
                        intt.putExtra("videoImage", pic.get(place));
                        intt.putExtra("videoChannelID", channel_id.get(place));
                        startActivity(intt);
                        finish();
                    } else {
                        show_internet_popup();
                    }
                    //Toast.makeText(gallery.this, tv.getText().toString().trim(),Toast.LENGTH_SHORT).show();
                }
            });
            myLayout.addView(hiddenInfo);
        }
    }

    void addAtList3() {


        final ArrayList<String> pic = new ArrayList<>();
        final ArrayList<String> text = new ArrayList<>();
        final ArrayList<String> vid_link = new ArrayList<>();
        final ArrayList<Integer> channel_id = new ArrayList<>();
        for (int i = 0; i < VideoChannelID.size(); i++) {
            if (VideoChannelID.get(i) == 2) {
                pic.add(VideoImage.get(i));
                text.add(VideoName.get(i));
                vid_link.add(VideoLink.get(i));
                channel_id.add(VideoChannelID.get(i));
            }
        }

        //int pic[] = {R.mipmap.list_11, R.mipmap.list_12, R.mipmap.list_13, R.mipmap.list_14, R.mipmap.list_15, R.mipmap.list_16};
        //String txt[] = {"A", "B", "C", "D", "E", "F"};
        for (int i = 0; i < pic.size(); i++) {
            LinearLayout myLayout = (LinearLayout) findViewById(R.id.list3);
            View hiddenInfo = getLayoutInflater().inflate(R.layout.child_horizontal_lv, myLayout, false);

            //Get References to the ImageView
            ImageView iv = (ImageView) hiddenInfo.findViewById(R.id.iv);
            final TextView tv = (TextView) hiddenInfo.findViewById(R.id.tv);
            // Update the ImageView
            Picasso.with(this)
                    .load(pic.get(i))
                    .placeholder(R.mipmap.list_2) // optional
                    .error(R.mipmap.list_3).memoryPolicy(MemoryPolicy.NO_CACHE)            // optional
                    .into(iv);

            //iv.setImageResource(pic[i]);
            tv.setText(text.get(i));
            final int place = i;
            iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (new check_internet().check(gallery.this)) {
                        Intent intt = new Intent(gallery.this, VideoPlayerActivity.class);
                        intt.putExtra("videoLink", vid_link.get(place));
                        intt.putExtra("videoName", text.get(place));
                        intt.putExtra("videoImage", pic.get(place));
                        intt.putExtra("videoChannelID", channel_id.get(place));
                        startActivity(intt);
                        finish();
                    } else {
                        show_internet_popup();
                    }
                    //Toast.makeText(gallery.this, tv.getText().toString().trim(),Toast.LENGTH_SHORT).show();
                }
            });
            myLayout.addView(hiddenInfo);
        }

    }

    void addAtList4() {
        int pic[] = {R.mipmap.list_21, R.mipmap.list_22, R.mipmap.list_23, R.mipmap.list_24, R.mipmap.list_25, R.mipmap.list_26, R.mipmap.list_27, R.mipmap.list_28, R.mipmap.list_29};
        String txt[] = {"A", "B", "C", "D", "E", "F", "G", "H", "I"};
        for (int i = 0; i < 9; i++) {
            LinearLayout myLayout = (LinearLayout) findViewById(R.id.list4);
            View hiddenInfo = getLayoutInflater().inflate(R.layout.child_horizontal_lv, myLayout, false);

            //Get References to the ImageView
            ImageView iv = (ImageView) hiddenInfo.findViewById(R.id.iv);
            final TextView tv = (TextView) hiddenInfo.findViewById(R.id.tv);
            // Update the ImageView
            iv.setImageResource(pic[i]);
            tv.setText(txt[i]);
            iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (new check_internet().check(gallery.this)) {
                        Intent intt = new Intent(gallery.this, VideoPlayerActivity.class);
                        intt.putExtra("videoLink", "http://scripton.in/mobiplay/android_connect/videos/3.3gp");
                        startActivity(intt);
                        finish();
                    } else {
                        show_internet_popup();
                    }
                    //Toast.makeText(gallery.this, tv.getText().toString().trim(),Toast.LENGTH_SHORT).show();
                }
            });
            myLayout.addView(hiddenInfo);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    void hideStatuBar() {
        if (Build.VERSION.SDK_INT < 16) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else {
            View decorView = getWindow().getDecorView();
// Hide the status bar.
            int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }

    @Override
    public void onBackPressed() {
        if (new check_internet().check(this)) {
            startActivity(new Intent(gallery.this, MainActivity.class));
            finish();
        } else {
            show_internet_popup();
        }
    }

    void show_internet_popup() {
        SweetAlertDialog ss = new SweetAlertDialog(gallery.this, SweetAlertDialog.ERROR_TYPE);
        ss.setTitleText("Oops...")
                .setContentText("Check Internet Connection !").show();
    }

}

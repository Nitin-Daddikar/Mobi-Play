package com.scripton.in.test;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.scripton.in.test.SQLiteDatabase.galleryList;
import com.scripton.in.test.SQLiteDatabase.galleryVideos;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.concurrent.TimeUnit;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by Nitin on 5/23/2016.
 */
public class splash extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
        final ImageView main = (ImageView) findViewById(R.id.main);
//        main.setVisibility(View.GONE);
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                main.setVisibility(View.VISIBLE);
//            }
//        }, 1000);
//new SlideInAnimation(this.card).setDirection(3).animate();
//        YoYo.with(Techniques.SlideInDown)
//                .duration(2000)
//                .playOn(main);
//        YoYo.with(Techniques.Pulse)
//                .duration(1000)
//                .delay(2000)
//                .playOn(findViewById(R.id.main));
        deleteCache();
        get_galleryList();

    }


    void get_galleryList() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, new Apis().galleryList,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("mobiplay", response);
                        showJSON_galleryList(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        if (!(splash.this).isFinishing()) {

                            SweetAlertDialog sd = new SweetAlertDialog(splash.this, SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText("Oops...")
                                    .setContentText("Something went wrong!");
                            sd.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    onBackPressed();
                                }
                            });
                            sd.show();
                        }
                    }
                }
        )
//        {
//            @Override
//            protected Map<String, String> getParams() {
//                Map<String, String> params = new HashMap<String, String>();
//                params.put("email", email);
//                params.put("pass", pass);
//                return params;
//            }
//
//        }
                ;

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                (int) TimeUnit.SECONDS.toMillis(20),
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    synchronized private void showJSON_galleryList(String response) {
        try {
            galleryList cList = new galleryList(this);
            cList.open();
            JSONArray jArray = new JSONArray(response);
            for (int i = 0; i < jArray.length(); i++) {
                JSONObject json_data = jArray.getJSONObject(i);
                cList.createEntry(json_data.getString("id"), json_data.getString("name"));
            }
            cList.close();
            get_videoList();
        } catch (Exception e) {
            Toast.makeText(splash.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            //Toast.makeText(splash.this, "Check Internet Connection..!!!", Toast.LENGTH_SHORT).show();
        }
    }


    void get_videoList() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, new Apis().videoList,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("mobiplay", response);
                        showJSON_videoList(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        if (!(splash.this).isFinishing()) {

                            SweetAlertDialog sd = new SweetAlertDialog(splash.this, SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText("Oops...")
                                    .setContentText("Something went wrong!");
                            sd.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    onBackPressed();
                                }
                            });
                            sd.show();
                        }
                    }
                }
        )
//        {
//            @Override
//            protected Map<String, String> getParams() {
//                Map<String, String> params = new HashMap<String, String>();
//                params.put("email", email);
//                params.put("pass", pass);
//                return params;
//            }
//
//        }
                ;

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                (int) TimeUnit.SECONDS.toMillis(20),
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }


    synchronized private void showJSON_videoList(String response) {
        try {
            galleryVideos vList = new galleryVideos(this);
            vList.open();
            JSONArray jArray = new JSONArray(response);
            for (int i = 0; i < jArray.length(); i++) {
                JSONObject json_data = jArray.getJSONObject(i);
                vList.createEntry(json_data.getString("id"), json_data.getString("name"), json_data.getString("image"), json_data.getString("video_link"), json_data.getString("channel_id"));
            }
            vList.close();
            if (new check_internet().check(splash.this)) {

                SharedPreferences view_sharedpreferences = getSharedPreferences("login", Context.MODE_PRIVATE);
                if (view_sharedpreferences.getInt("status", 100) == 1) {
                    Intent i;
                    i = new Intent(splash.this, ads.class);
                    startActivity(i);
                    finish();
                } else {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent i;
                            i = new Intent(splash.this, login.class);
                            startActivity(i);
                            finish();
                        }
                    }, 4000);
                }
            } else {
                SweetAlertDialog ss = new SweetAlertDialog(splash.this, SweetAlertDialog.ERROR_TYPE);
                ss.setTitleText("Oops...")
                        .setContentText("Check Internet Connection !")
                        .setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialogInterface) {
                                finish();
                            }
                        });
                ss.show();
            }
        } catch (Exception e) {
            Toast.makeText(splash.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            //Toast.makeText(splash.this, "Check Internet Connection..!!!", Toast.LENGTH_SHORT).show();
        }
    }

    public void deleteCache() {
        File fdeletess[] = new File[2];
        fdeletess[0] = new File(getFilesDir().getParent() + "/databases/galleryList");
        fdeletess[1] = new File(getFilesDir().getParent() + "/databases/galleryVideos");
        for(int i = 0 ; i <fdeletess.length;i++){
            if (fdeletess[i].exists()) {
                fdeletess[i].delete();
            }
        }
    }
}

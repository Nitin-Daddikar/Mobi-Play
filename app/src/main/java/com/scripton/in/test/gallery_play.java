package com.scripton.in.test;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.VideoView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.roger.gifloadinglibrary.GifLoadingView;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by Nitin on 5/30/2016.
 */
public class gallery_play extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    VideoView videoview;
    LinearLayout lay_menu;
    boolean visible = true;
    final Handler timeoutHandler = new Handler();
    Runnable finalizer;
    private GifLoadingView mGifLoadingView;
    String current_url = "";

    GoogleApiClient google_api_client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        google_api_client = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API, Plus.PlusOptions.builder().build())
                .addScope(Plus.SCOPE_PLUS_LOGIN)
                .addScope(Plus.SCOPE_PLUS_PROFILE)
                .build();
        hideStatuBar();
        setContentView(R.layout.gallery_play);
        videoview = (VideoView) findViewById(R.id.VideoView);
        lay_menu = (LinearLayout) findViewById(R.id.lay_menu);
        getSupportActionBar().hide();

        final FloatingActionButton fab_logout = (FloatingActionButton) findViewById(R.id.fab_logout);
        fab_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logout();
            }
        });
        final FloatingActionButton fab_gallery = (FloatingActionButton) findViewById(R.id.fab_gallery);
        fab_gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (new check_internet().check(gallery_play.this)) {
                    startActivity(new Intent(gallery_play.this, MainActivity.class));
                    finish();
                } else {
                    show_internet_popup();
                }
                hideStatuBar();
            }
        });
        final FloatingActionButton fab_back = (FloatingActionButton) findViewById(R.id.fab_back);
        fab_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        final FloatingActionButton fab_settings = (FloatingActionButton) findViewById(R.id.fab_settings);
        fab_settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(gallery_play.this, settings.class));
                finish();
            }
        });
        final FloatingActionButton fab_like = (FloatingActionButton) findViewById(R.id.fab_like);
        fab_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (new check_internet().check(gallery_play.this)) {
                    new SweetAlertDialog(gallery_play.this)
                            .setTitleText("Added to favorite list")
                            .show();
                    System.gc();
                } else {
                    show_internet_popup();
                }
                hideStatuBar();
            }
        });
//        new Handler().postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    YoYo.with(Techniques.FadeOut)
//                            .duration(700)
//                            .playOn(lay_menu);
//                    visible = false;
//                }
//            }, 3000);
//
//        final ProgressDialog pDialog = new ProgressDialog(MainActivity.this);
//        // Set progressbar title
//        pDialog.setTitle("Android Video Streaming Tutorial");
//        // Set progressbar message
//        pDialog.setMessage("Buffering...");
//        pDialog.setIndeterminate(false);
//        pDialog.setCancelable(false);
//        // Show progressbar
//        pDialog.show();
        mGifLoadingView = new GifLoadingView();
        mGifLoadingView.setImageResource(R.drawable.loading);
        mGifLoadingView.show(getFragmentManager(), "");
        hideStatuBar();
//        pDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
//            @Override
//            public void onCancel(DialogInterface dialogInterface) {
//
//            }
//        });
        try {
            // Start the MediaController
            MediaController mediacontroller = new MediaController(
                    gallery_play.this);
            mediacontroller.setAnchorView(videoview);
            // Get the URL from String VideoURL
            Uri video = Uri.parse("http://shreyashjadhav000.16mb.com/5.3gp");
            videoview.setMediaController(mediacontroller);
            videoview.setVideoURI(video);
           // videoview.setMediaController(null);

        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }

        videoview.requestFocus();
        videoview.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            // Close the progress bar and play the video
            public void onPrepared(MediaPlayer mp) {
                mGifLoadingView.dismiss();
                videoview.start();
                current_url = "http://scripton.in/mobiplay/android_connect/videos/2.3gp";
                mp.setLooping(true);
            }
        });
        videoview.setOnErrorListener(new MediaPlayer.OnErrorListener() {

            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                mGifLoadingView.dismiss();
                new SweetAlertDialog(gallery_play.this)
                        .setTitleText("Can't play this video..!!!")
                        .show();
                System.gc();
                return false;
            }

        });
        videoview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideNvisible();
            }
        });

        visible = true;
        setThreeSecTime();

        // call  again and again
//        final Handler handler = new Handler();
//        handler.postDelayed(new Runnable() {
//
//            @Override
//            public void run() {
//                set();
//                handler.postDelayed(this, 3000);
//            }
//        }, 3000);

    }

    @Override
    protected void onResume() {
        hideStatuBar();
        super.onResume();
        if (videoview != null) {
            videoview.start();
        }
        if (google_api_client.isConnected()) {
            google_api_client.connect();
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        google_api_client.connect();
    }


    @Override
    protected void onStop() {
        super.onStop();
        if (videoview != null) {
            videoview.pause();
        }
        if (google_api_client.isConnected()) {
            google_api_client.disconnect();
        }

    }

    public void logout() {
        SharedPreferences view_sharedpreferences = getSharedPreferences("login", Context.MODE_PRIVATE);
        switch (view_sharedpreferences.getInt("account_type", 100)) {
            case 1:   // facebook
                new GraphRequest(AccessToken.getCurrentAccessToken(), "/me/permissions/", null, HttpMethod.DELETE, new GraphRequest
                        .Callback() {
                    @Override
                    public void onCompleted(GraphResponse graphResponse) {
                        LoginManager.getInstance().logOut();
                    }
                }).executeAsync();
                break;
            case 2:   // google plus

                if (google_api_client.isConnected()) {
                    Plus.AccountApi.clearDefaultAccount(google_api_client);
                    google_api_client.disconnect();
                    google_api_client.connect();
                    //changeUI(false);
                }
                break;
            case 3:    // general


                break;
        }


        SharedPreferences settings = getSharedPreferences("login", 0);
        if (settings.contains("status")) {
            SharedPreferences.Editor editor = settings.edit();
            editor.clear();
            editor.commit();
        }
        startActivity(new Intent(gallery_play.this, login.class));
        finish();

//        if (AccessToken.getCurrentAccessToken() == null) {
//            Toast.makeText(this, "alread logged out", Toast.LENGTH_SHORT).show();
//            return; // already logged out
//        }


    }


    //public void hideNvisible(View v) {
    public void hideNvisible() {
        //set();
        if (visible == true) {

            YoYo.with(Techniques.SlideOutRight)
                    .duration(700)
                    .playOn(lay_menu);
            visible = false;
        } else {

            YoYo.with(Techniques.FadeIn)
                    .duration(700)
                    .playOn(lay_menu);
            visible = true;
        }
    }

    // check layout visible or not if yes make it visible n wise varsa
    void set() {
        if (visible == true) {

            YoYo.with(Techniques.SlideOutRight)
                    .duration(700)
                    .playOn(lay_menu);
//            YoYo.with(Techniques.SlideOutLeft)
//                    .duration(700)
//                    .playOn(lay_back);
            visible = false;

            timeoutHandler.removeCallbacks(finalizer);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    lay_menu.setVisibility(View.GONE);
                    // lay_back.setVisibility(View.GONE);
                }
            }, 700);
        } else {
            lay_menu.setVisibility(View.VISIBLE);
            //lay_back.setVisibility(View.VISIBLE);
            YoYo.with(Techniques.FadeIn)
                    .duration(700)
                    .playOn(lay_menu);
//            YoYo.with(Techniques.FadeIn)
//                    .duration(700)
//                    .playOn(lay_back);
            visible = true;
            setThreeSecTime();
//            new Handler().postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    lay_menu.setVisibility(View.VISIBLE);
//                }
//            }, 700);
        }
    }

    // set layout invisible after 3 sec of visible
    void setThreeSecTime() {
//        if (visible == true) {
//            new Handler().postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    if (visible == true) {
//                        set();
//                    }
//                }
//            }, 3000);
//        }
        if (visible == true) {

            finalizer = new Runnable() {
                public void run() {
                    if (visible == true) {
                        set();
                    }
                }
            };
            timeoutHandler.postDelayed(finalizer, 3000);
        }
    }

    public void menu_lay_click(View v) {
        visible = false;
        set();
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
            startActivity(new Intent(gallery_play.this, gallery.class));
            finish();
        } else {
            show_internet_popup();
        }
    }

    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    void show_internet_popup() {
        SweetAlertDialog ss = new SweetAlertDialog(gallery_play.this, SweetAlertDialog.ERROR_TYPE);
        ss.setTitleText("Oops...")
                .setContentText("Check Internet Connection !").show();
    }

}
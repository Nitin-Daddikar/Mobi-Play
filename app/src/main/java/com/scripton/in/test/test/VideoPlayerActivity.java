package com.scripton.in.test.test;

/**
 * Created by Nitin on 8/6/2016.
 */

import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.MediaController;

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
import com.scripton.in.test.MainActivity;
import com.scripton.in.test.R;
import com.scripton.in.test.SQLiteDatabase.favoriteVideos;
import com.scripton.in.test.check_internet;
import com.scripton.in.test.gallery;
import com.scripton.in.test.login;
import com.scripton.in.test.settings;

import org.json.JSONObject;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class VideoPlayerActivity extends AppCompatActivity implements SurfaceHolder.Callback, MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, VideoControllerView.MediaPlayerControl, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    SurfaceView videoSurface;
    MediaPlayer player;
    VideoControllerView controller;


    LinearLayout lay_menu;
    boolean visible = true;
    final Handler timeoutHandler = new Handler();
    Runnable finalizer;
    private GifLoadingView mGifLoadingView;

    GoogleApiClient google_api_client;
    String videoLink,videoName,videoImage;
    int videoChannelID;

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
        setContentView(R.layout.activity_video_player);
        lay_menu = (LinearLayout) findViewById(R.id.lay_menu);
//        getSupportActionBar().hide();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            videoLink = extras.getString("videoLink");
            videoName = extras.getString("videoName");
            videoImage = extras.getString("videoImage");
            videoChannelID = extras.getInt("videoChannelID");
            //The key argument here must match that used in the other activity
        }
        videoSurface = (SurfaceView) findViewById(R.id.videoSurface);
        SurfaceHolder videoHolder = videoSurface.getHolder();
        videoHolder.addCallback(this);

        player = new MediaPlayer();
        controller = new VideoControllerView(this);

        mGifLoadingView = new GifLoadingView();
        mGifLoadingView.setImageResource(R.drawable.loading);
        mGifLoadingView.show(getFragmentManager(), "");
        hideStatuBar();
        try {
            player.setAudioStreamType(AudioManager.STREAM_MUSIC);
            player.setDataSource(this, Uri.parse(videoLink));
            player.setOnPreparedListener(this);
            player.setOnErrorListener(this);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


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
                if (new check_internet().check(VideoPlayerActivity.this)) {
                    startActivity(new Intent(VideoPlayerActivity.this, MainActivity.class));
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
                startActivity(new Intent(VideoPlayerActivity.this, settings.class));
                finish();
            }
        });
        final FloatingActionButton fab_like = (FloatingActionButton) findViewById(R.id.fab_like);
        fab_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                favoriteVideos fav = new favoriteVideos(getBaseContext());
                fav.open();
                fav.createEntry(videoName,videoImage,videoLink,videoChannelID);
                fav.close();
                new SweetAlertDialog(VideoPlayerActivity.this)
                        .setTitleText("Added to favorite list")
                        .show();

                System.gc();
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
//        pDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
//            @Override
//            public void onCancel(DialogInterface dialogInterface) {
//
//            }
//        });
//        try {
//            // Start the MediaController
//            MediaController mediacontroller = new MediaController(
//                    VideoPlayerActivity.this);
//            mediacontroller.setAnchorView(videoview);
//            // Get the URL from String VideoURL
//            Uri video = Uri.parse("http://shreyashjadhav000.16mb.com/5.3gp");
//            videoview.setMediaController(mediacontroller);
//            videoview.setVideoURI(video);
//            // videoview.setMediaController(null);
//
//        } catch (Exception e) {
//            Log.e("Error", e.getMessage());
//            e.printStackTrace();
//        }
//
//        videoview.requestFocus();
//        videoview.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//            // Close the progress bar and play the video
//            public void onPrepared(MediaPlayer mp) {
//                mGifLoadingView.dismiss();
//                videoview.start();
//                current_url = "http://shreyashjadhav000.16mb.com/2.3gp";
//                mp.setLooping(true);
//            }
//        });
//        videoview.setOnErrorListener(new MediaPlayer.OnErrorListener() {
//
//            @Override
//            public boolean onError(MediaPlayer mp, int what, int extra) {
//                mGifLoadingView.dismiss();
//                new SweetAlertDialog(VideoPlayerActivity.this)
//                        .setTitleText("Can't play this video..!!!")
//                        .show();
//                System.gc();
//                return false;
//            }
//
//        });
//        videoview.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                hideNvisible();
//            }
//        });

        visible = false;

        lay_menu.setVisibility(View.GONE);
        //setThreeSecTime();

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
    public boolean onTouchEvent(MotionEvent event) {
        hideNvisible();
        return false;
    }

    // Implement SurfaceHolder.Callback
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        player.setDisplay(holder);
        player.prepareAsync();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }
    // End SurfaceHolder.Callback

    // Implement MediaPlayer.OnPreparedListener
    @Override
    public void onPrepared(MediaPlayer mp) {
        controller.setMediaPlayer(this);
        controller.setAnchorView((FrameLayout) findViewById(R.id.videoSurfaceContainer));
        player.start();
        mGifLoadingView.dismiss();
        mp.setLooping(true);
    }
    // End MediaPlayer.OnPreparedListener

    // Implement VideoMediaController.MediaPlayerControl
    @Override
    public boolean canPause() {
        return true;
    }

    @Override
    public boolean canSeekBackward() {
        return true;
    }

    @Override
    public boolean canSeekForward() {
        return true;
    }

    @Override
    public int getBufferPercentage() {
        return 0;
    }

    @Override
    public int getCurrentPosition() {
        return player.getCurrentPosition();
    }

    @Override
    public int getDuration() {
        return player.getDuration();
    }

    @Override
    public boolean isPlaying() {
        return player.isPlaying();
    }

    @Override
    public void pause() {
        player.pause();
    }

    @Override
    public void seekTo(int i) {
        player.seekTo(i);
    }

    @Override
    public void start() {
        player.start();
    }

    @Override
    public boolean isFullScreen() {
        return false;
    }

    @Override
    public void toggleFullScreen() {

    }

    @Override
    protected void onResume() {
        hideStatuBar();
        super.onResume();
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
        startActivity(new Intent(VideoPlayerActivity.this, login.class));
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

            controller.hide();
            lay_menu.setVisibility(View.GONE);
//            YoYo.with(Techniques.SlideOutRight)
//                    .duration(700)
//                    .playOn(lay_menu);
            visible = false;
        } else {
            controller.show();
            lay_menu.setVisibility(View.VISIBLE);

//            YoYo.with(Techniques.FadeIn)
//                    .duration(700)
//                    .playOn(lay_menu);
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
            if (player != null) {
                player.pause();
            }
            startActivity(new Intent(VideoPlayerActivity.this, gallery.class));
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
        SweetAlertDialog ss = new SweetAlertDialog(VideoPlayerActivity.this, SweetAlertDialog.ERROR_TYPE);
        ss.setTitleText("Oops...")
                .setContentText("Check Internet Connection !").show();
    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
        if (!(VideoPlayerActivity.this).isFinishing()) {
            mGifLoadingView.dismiss();
            new SweetAlertDialog(VideoPlayerActivity.this)
                    .setTitleText("Can't play this video..!!!")
                    .show();
            System.gc();
        }
        return false;
    }
    // End VideoMediaController.MediaPlayerControl

}
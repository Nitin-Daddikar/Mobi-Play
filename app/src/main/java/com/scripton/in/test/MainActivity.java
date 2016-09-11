package com.scripton.in.test;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.MediaController;
import android.widget.Toast;
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
import com.kaopiz.kprogresshud.KProgressHUD;
import com.red5pro.streaming.R5Connection;
import com.red5pro.streaming.R5Stream;
import com.red5pro.streaming.R5StreamProtocol;
import com.red5pro.streaming.config.R5Configuration;
import com.red5pro.streaming.event.R5ConnectionEvent;
import com.red5pro.streaming.event.R5ConnectionListener;
import com.red5pro.streaming.view.R5VideoView;
import com.roger.gifloadinglibrary.GifLoadingView;

import java.util.ArrayList;
import java.util.Arrays;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class MainActivity extends AppCompatActivity implements R5ConnectionListener, ListAdapter.customButtonListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    VideoView videoview;
    LinearLayout lay_menu;
    boolean visible = true;
    String current_url = "";
    private GifLoadingView mGifLoadingView;
    KProgressHUD k;
    GoogleApiClient google_api_client;
    Runnable mRunnable;
    Handler mHandler = new Handler();
    R5VideoView r5VideoView;
    protected R5Stream subscribe;
    Thread retryThread;
    public static boolean swapped = false;


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
        setContentView(R.layout.activity_main);

        ListView listView = (ListView) findViewById(R.id.channels_list);
        videoview = (VideoView) findViewById(R.id.VideoView);
        r5VideoView = (R5VideoView) findViewById(R.id.video);
        lay_menu = (LinearLayout) findViewById(R.id.lay_menu);
        r5VideoView.setVisibility(View.GONE);
        videoview.setVisibility(View.VISIBLE);
        getSupportActionBar().hide();

        final FloatingActionButton fab_settings = (FloatingActionButton) findViewById(R.id.fab_settings);
        fab_settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, settings.class));
                finish();
            }
        });
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
                startActivity(new Intent(MainActivity.this, gallery.class));
                finish();
            }
        });

//        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
//        pDialog.getProgressHelper().setBarColor(Color.parseColor("#dfac0e"));
//        pDialog.setTitleText("Loading");
//        pDialog.setCancelable(false);
//        pDialog.show();
//        hideStatuBar();
//        k  = KProgressHUD.create(MainActivity.this)
//                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
//                .setLabel("Please wait")
//                .setCancellable(false)
//                .setAnimationSpeed(2)
//                .setDimAmount(0.5f);
//        k.show();
        mGifLoadingView = new GifLoadingView();
        mGifLoadingView.setImageResource(R.drawable.loading);
        mGifLoadingView.show(getFragmentManager(), "");

//        pDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
//            @Override
//            public void onCancel(DialogInterface dialogInterface) {
//
//            }
//        });
        try {
            // Start the MediaController
            MediaController mediacontroller = new MediaController(
                    MainActivity.this);
            mediacontroller.setAnchorView(videoview);
            // Get the URL from String VideoURL
            //String path = "android.resource://" + getPackageName() + "/" + R.raw.one;
            Uri video = Uri.parse("http://scripton.in/mobiplay/android_connect/videos/2.3gp");
            videoview.setMediaController(mediacontroller);
            //videoview.setVideoURI(Uri.parse(path));
            videoview.setVideoURI(video);
            videoview.setMediaController(null);

        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }

        videoview.requestFocus();
        videoview.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            // Close the progress bar and play the video
            public void onPrepared(MediaPlayer mp) {
                mGifLoadingView.dismiss();
                //k.dismiss();
                // pDialog.dismiss();
                videoview.start();
                current_url = "http://scripton.in/mobiplay/android_connect/videos/2.3gp";
                mp.setLooping(true);
            }
        });
        videoview.setOnErrorListener(new MediaPlayer.OnErrorListener() {

            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {

                mGifLoadingView.dismiss();
                //k.dismiss();
                // pDialog.dismiss();
                new SweetAlertDialog(MainActivity.this)
                        .setTitleText("Can't play this video..!!!")
                        .show();
                System.gc();
                return true;
            }

        });
        visible = true;
        setThreeSecTime();
        int[] img = {R.drawable.capture2, R.drawable.capture1, R.drawable.capture2, R.drawable.capture1, R.drawable.capture2};
        String[] txt = {"Live Streaming", "Zee Anmol", "Star Plus", "Zee TV", "Life OK"};
        ListAdapter adapter = new ListAdapter(MainActivity.this, new ArrayList<String>(Arrays.asList(txt)), img);
        adapter.setCustomButtonListner(MainActivity.this);
        listView.setAdapter(adapter);

    }


    @Override
    public void onButtonClickListnerID(int position, String value) {
        if (new check_internet().check(MainActivity.this)) {

            r5VideoView.setVisibility(View.GONE);
            videoview.setVisibility(View.VISIBLE);
            if (subscribe != null) {
                subscribe.stop();
            }
            switch (position) {
                case 0:
                    r5VideoView.setVisibility(View.VISIBLE);
                    videoview.setVisibility(View.GONE);
                    if (videoview != null) {
                        videoview.pause();
                    }
                    if (subscribe == null) {
                        Resources res = getResources();

                        //Create the configuration from the values.xml
                        R5Configuration config = new R5Configuration(R5StreamProtocol.RTSP, res.getString(R.string.domain), res.getInteger(R.integer.port), res.getString(R.string.context), 0.5f);
                        R5Connection connection = new R5Connection(config);

                        //setup a new stream using the connection
                        subscribe = new R5Stream(connection);

                        subscribe.client = this;
                        subscribe.setListener(MainActivity.this);

                        //show all logging
                        //subscribe.setLogLevel(R5Stream.LOG_LEVEL_DEBUG);

                        //find the view and attach the stream
                        R5VideoView r5VideoView = (R5VideoView) findViewById(R.id.video);
                        r5VideoView.attachStream(subscribe);
                        //r5VideoView.showDebugView(res.getBoolean(R.bool.debugView));

                        subscribe.play(getStream1());
                    }else{
                        subscribe.stop();
                    }
                    return;
                // current_url = "http://shreyashjadhav000.16mb.com/1.3gp";
                //break;
                case 1:
                    current_url = "http://scripton.in/mobiplay/android_connect/videos/2.3gp";
                    break;
                case 2:
                    current_url = "http://scripton.in/mobiplay/android_connect/videos/3.3gp";
                    break;
                case 3:
                    current_url = "http://scripton.in/mobiplay/android_connect/videos/4.3gp";
                    break;
                case 4:
                    current_url = "http://scripton.in/mobiplay/android_connect/videos/5.3gp";
                    break;
            }
            videoview.pause();
            // pDialog.show();
            hideStatuBar();
            Uri video = Uri.parse(current_url);
            videoview.setVideoURI(video);
            videoview.requestFocus();
            videoview.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                // Close the progress bar and play the video
                public void onPrepared(MediaPlayer mp) {

                    mGifLoadingView.dismiss();
                    //k.dismiss();
                    //pDialog.dismiss();
                    hideStatuBar();
                    videoview.start();
                    current_url = "https://www.youtube.com/embed/Pg8wi-vWLSY?autoplay=1&vq=small";
                    mp.setLooping(true);
                }
            });
            videoview.setOnErrorListener(new MediaPlayer.OnErrorListener() {

                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {

                    mGifLoadingView.dismiss();
                    //k.dismiss();
                    //pDialog.dismiss();
                    hideStatuBar();
                    new SweetAlertDialog(MainActivity.this)
                            .setTitleText("Can't play this video..!!!")
                            .show();
                    System.gc();
                    return true;
                }

            });
        } else {
            SweetAlertDialog ss = new SweetAlertDialog(MainActivity.this, SweetAlertDialog.ERROR_TYPE);
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
    }

    protected String getStream1() {
        if (!swapped) return getString(R.string.stream1);
        else return getString(R.string.stream2);
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

        if (subscribe != null)
            subscribe.stop();

        subscribe = null;


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
        startActivity(new Intent(MainActivity.this, login.class));
        finish();

//        if (AccessToken.getCurrentAccessToken() == null) {
//            Toast.makeText(this, "alread logged out", Toast.LENGTH_SHORT).show();
//            return; // already logged out
//        }


    }


    public void hideNvisible(View v) {
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
            visible = false;
            lay_menu.setEnabled(false);
            mHandler.removeCallbacks(mRunnable);
//            new Handler().postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    lay_menu.setVisibility(View.GONE);
//                }
//            }, 700);
        } else {

            lay_menu.setEnabled(true);

            YoYo.with(Techniques.FadeIn)
                    .duration(700)
                    .playOn(lay_menu);
            visible = true;
            mRunnable = new Runnable() {

                @Override
                public void run() {
                    if (visible) {
                        set();
                    }

                }
            };
            mHandler.postDelayed(mRunnable, 3000);
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
        if (visible == true) {

            lay_menu.setEnabled(true);

            YoYo.with(Techniques.FadeIn)
                    .duration(700)
                    .playOn(lay_menu);
            visible = true;
            mRunnable = new Runnable() {

                @Override
                public void run() {
                    if (visible) {
                        set();
                    }

                }
            };
            mHandler.postDelayed(mRunnable, 3000);
        }

    }

    @Override
    public void onBackPressed() {

        finish();
    }

    public void menu_lay_click() {
        mHandler.removeCallbacks(mRunnable);
        if (visible == true) {
            mRunnable = new Runnable() {

                @Override
                public void run() {
                    if (visible) {
                        set();
                    }

                }
            };
            mHandler.postDelayed(mRunnable, 3000);
        }
//        lay_menu.setEnabled(true);
//        YoYo.with(Techniques.FadeIn)
//                .duration(700)
//                .playOn(lay_menu);
//        visible = true;
//        setThreeSecTime();
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
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onConnectionEvent(R5ConnectionEvent r5ConnectionEvent) {

        if (r5ConnectionEvent == R5ConnectionEvent.CLOSE) {
            retryThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    if (!Thread.interrupted() && subscribe != null) {

                        try {
                            Thread.sleep(8000);

                            subscribe.stop();
                            subscribe.play(getStream1());
                        } catch (Exception e) {
                            System.out.println("failed to reconnect");
                        }
                    }
                }
            });
            retryThread.start();
        }
    }
}

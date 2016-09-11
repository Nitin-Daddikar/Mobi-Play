package com.scripton.in.test;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.MediaController;
import android.widget.VideoView;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.roger.gifloadinglibrary.GifLoadingView;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by Nitin on 7/31/2016.
 */
public class ads extends AppCompatActivity {
    VideoView videoview;
    private GifLoadingView mGifLoadingView;
    String current_url;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hideStatuBar();
        setContentView(R.layout.ads);
        getSupportActionBar().hide();
        videoview = (VideoView) findViewById(R.id.VideoView);

        final FloatingActionButton fab_skip= (FloatingActionButton) findViewById(R.id.skip);
        fab_skip.setVisibility(View.GONE);
        fab_skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ads.this,MainActivity.class));
                finish();
            }
        });

        new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    fab_skip.setVisibility(View.VISIBLE);
                }
            }, 3000);

        mGifLoadingView = new GifLoadingView();
        mGifLoadingView.setImageResource(R.drawable.loading);
        mGifLoadingView.show(getFragmentManager(), "");

        try {
            // Start the MediaController
            MediaController mediacontroller = new MediaController(
                    ads.this);
            mediacontroller.setAnchorView(videoview);
            // Get the URL from String VideoURL
            Uri video = Uri.parse("http://scripton.in/mobiplay/android_connect/ads/mentosAd.3gp");
            videoview.setMediaController(mediacontroller);
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
                videoview.start();
                current_url = "http://shreyashjadhav000.16mb.com/5.3gp";
                mp.setLooping(false);
            }
        });
        videoview.setOnErrorListener(new MediaPlayer.OnErrorListener() {

            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                mGifLoadingView.dismiss();
                new SweetAlertDialog(ads.this)
                        .setTitleText("Can't play this video..!!!")
                        .show();
                System.gc();
                return false;
            }

        });
        videoview.setOnCompletionListener(new MediaPlayer.OnCompletionListener()
        {
            @Override
            public void onCompletion(MediaPlayer mp)
            {
                startActivity(new Intent(ads.this,MainActivity.class));
                finish();
            }
        });

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
    }
}

package com.scripton.in.test;

import android.app.ActionBar;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.getbase.floatingactionbutton.FloatingActionButton;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by Nitin on 6/4/2016.
 */
public class url_browser extends AppCompatActivity {

    private WebView mWebView;
    SweetAlertDialog pDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.url_browser);
        getSupportActionBar().hide();
        hideStatuBar();
        Bundle bundle = getIntent().getExtras();
        String url = bundle.getString("url");

        final FloatingActionButton back_btn = (FloatingActionButton) findViewById(R.id.fab_back);
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //onBackPressed();
                finish();
            }
        });
        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#dfac0e"));
        pDialog.setTitleText("Loading....");
        pDialog.setCancelable(true);
        pDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                //onBackPressed();
                finish();
            }
        });
        pDialog.show();
        try {
            mWebView = (WebView) findViewById(R.id.activity_main_webview);
            WebSettings webSettings = mWebView.getSettings();
            webSettings.setJavaScriptEnabled(true);
            mWebView.loadUrl(url.trim());
            mWebView.setWebViewClient(new MyAppWebViewClient() {
                @Override
                public void onPageFinished(WebView view, String url) {
                    pDialog.dismiss();
                }
            });
        }
        catch(Exception e){

        }
    }

    @Override
    public void onBackPressed() {
        try {
            if (mWebView.canGoBack()) {
                mWebView.goBack();
            } else {
                super.onBackPressed();
            }
        }catch (Exception ae){

        }
    }

    private class MyAppWebViewClient extends WebViewClient {


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

}


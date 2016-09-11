package com.scripton.in.test;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;

import com.getbase.floatingactionbutton.FloatingActionButton;

import cn.pedant.SweetAlert.SweetAlertDialog;
/**
 * Created by Nitin on 6/4/2016.
 */
public class about_us extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_us);
        getSupportActionBar().hide();
        hideStatuBar();
        final FloatingActionButton back_btn = (FloatingActionButton) findViewById(R.id.fab_back);
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        hideStatuBar();
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, settings.class));
        finish();
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

    public void termsNconditions(View v) {
        if (new check_internet().check(this)) {
            Intent browserIntent = new Intent(about_us.this, url_browser.class);
            browserIntent.putExtra("url", "http://demo.mahara.org/terms.php");
            startActivity(browserIntent);
        } else {
            show_internet_popup();
        }

    }

    public void privacyNpolicy(View v) {
        if (new check_internet().check(this)) {
            Intent browserIntent = new Intent(about_us.this, url_browser.class);
            browserIntent.putExtra("url", "https://www.nibusinessinfo.co.uk/content/sample-website-usage-terms-and-conditions");
            startActivity(browserIntent);
        } else {
            show_internet_popup();
        }
    }

    void show_internet_popup() {
        SweetAlertDialog ss = new SweetAlertDialog(about_us.this, SweetAlertDialog.ERROR_TYPE);
        ss.setTitleText("Oops...")
                .setContentText("Check Internet Connection !").show();
    }
}

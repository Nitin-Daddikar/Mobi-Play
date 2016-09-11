package com.scripton.in.test;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.readystatesoftware.viewbadger.BadgeView;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by Nitin on 5/31/2016.
 */
public class settings extends AppCompatActivity {
    boolean notiEnable = true;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hideStatuBar();
        setContentView(R.layout.settings);

        final FloatingActionButton back_btn = (FloatingActionButton) findViewById(R.id.fab_back);
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        getSupportActionBar().hide();
        final TextView tv_noti_text = (TextView) findViewById(R.id.tv_noti_text);
        final ImageView iv_noti_icon = (ImageView) findViewById(R.id.iv_noti_icon);
        LinearLayout enableDisableNoti = (LinearLayout) findViewById(R.id.enableDisableNoti);
        enableDisableNoti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (notiEnable) {
                    notiEnable= false;
                    iv_noti_icon.setImageResource(R.drawable.notifications_off);
                    tv_noti_text.setText("Notification : Disabled");
                    SharedPreferences.Editor edit_sharedpreferences = getSharedPreferences("general", Context.MODE_PRIVATE).edit();
                    edit_sharedpreferences.putBoolean("NotiOn", false);
                    edit_sharedpreferences.commit();
                    System.gc();
                } else {
                    notiEnable= true;
                    iv_noti_icon.setImageResource(R.drawable.notifications_on);
                    tv_noti_text.setText("Notification : Enabled");
                    SharedPreferences.Editor edit_sharedpreferences = getSharedPreferences("general", Context.MODE_PRIVATE).edit();
                    edit_sharedpreferences.putBoolean("NotiOn",true);
                    edit_sharedpreferences.commit();
                    System.gc();
                }
            }
        });

        SharedPreferences view_sharedpreferences = getSharedPreferences("general", Context.MODE_PRIVATE);
        int count = view_sharedpreferences.getInt("NoOfNoti",0);
        if(count != 0){
            View target = findViewById(R.id.iv_noti_count);
            BadgeView badge = new BadgeView(this, target);
            badge.setBadgePosition(BadgeView.POSITION_TOP_RIGHT);
            badge.setText(count+"");
            badge.show();
        }
    }

    public void openNoti(View v){
        SharedPreferences.Editor edit_sharedpreferences = getSharedPreferences("general", Context.MODE_PRIVATE).edit();
        edit_sharedpreferences.putInt("NoOfNoti", 0);
        edit_sharedpreferences.commit();
        System.gc();
    }
    public void openProfile(View v){
        startActivity(new Intent(settings.this, my_profile.class));
        finish();
    }
    public void videoQuality(View v){
        new MaterialDialog.Builder(this)
                .title("Select Video Quality")
                .items(R.array.video_quality)
                .itemsCallbackSingleChoice(1, new MaterialDialog.ListCallbackSingleChoice() {

                    @Override
                    public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        try {
                            new SweetAlertDialog(settings.this)
                                    .setTitleText("Your video quality will be " + text.toString().trim())
                                    .show();
                            System.gc();
                        }catch (Exception e){

                        }
                        /**
                         * If you use alwaysCallSingleChoiceCallback(), which is discussed below,
                         * returning false here won't allow the newly selected radio button to actually be selected.
                         **/
                        return true;
                    }
                })
                .positiveText("Done")
                .show();

    }

    public void about_us(View v){

       startActivity(new Intent(settings.this,about_us.class));
        finish();
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(settings.this, MainActivity.class));
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

}

package com.scripton.in.test;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.cocosw.bottomsheet.BottomSheet;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.meg7.widget.CircleImageView;
import com.squareup.picasso.Picasso;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import cn.pedant.SweetAlert.SweetAlertDialog;
//import com.squareup.picasso.Picasso;

/**
 * Created by Nitin on 6/1/2016.
 */
public class my_profile extends AppCompatActivity implements
        TimePickerDialog.OnTimeSetListener,
        DatePickerDialog.OnDateSetListener {
    EditText et_first_name, et_last_name;
    TextView tv_dob, tv_noti_text;
    CircleImageView iv_pro_pic;
    ImageView iv_noti_icon;
    boolean changePic = false;
    SweetAlertDialog pDialog;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_profile);
        final FloatingActionButton back_btn = (FloatingActionButton) findViewById(R.id.fab_back);
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        hideStatuBar();
        getSupportActionBar().hide();
        et_first_name = (EditText) findViewById(R.id.et_first_name);
        et_last_name = (EditText) findViewById(R.id.et_last_name);
        tv_dob = (TextView) findViewById(R.id.tv_dob);
        iv_pro_pic = (CircleImageView) findViewById(R.id.iv_pro_pic);
        check_info();
    }

    void check_info() {
        if (new check_internet().check(my_profile.this)) {
            SharedPreferences view_sharedpreferences = getSharedPreferences("login", Context.MODE_PRIVATE);
            et_first_name.setText(view_sharedpreferences.getString("first_name", ""));
            et_last_name.setText(view_sharedpreferences.getString("last_name", ""));
            tv_dob.setText(view_sharedpreferences.getString("bdate", ""));
            if (view_sharedpreferences.getInt("account_type", 10) == 3) {
                Button btn_change_pass = (Button) findViewById(R.id.btn_change_pass);
                btn_change_pass.setVisibility(View.VISIBLE);
            }
            setProfilePic(view_sharedpreferences.getInt("account_type", 10), view_sharedpreferences.getString("profile_pic", ""));
            System.gc();
        } else {
            SweetAlertDialog ss = new SweetAlertDialog(my_profile.this, SweetAlertDialog.ERROR_TYPE);
            ss.setTitleText("Oops...")
                    .setContentText("Check Internet Connection !").show();
        }
    }

    void setProfilePic(int type, final String url) {
        switch (type) {
            case 1:
                AsyncTask<Void, Void, Bitmap> t = new AsyncTask<Void, Void, Bitmap>() {
                    protected Bitmap doInBackground(Void... p) {
                        Bitmap bm = null;
                        try {
                            URL aURL = new URL(url);
                            URLConnection conn = aURL.openConnection();
                            conn.setUseCaches(true);
                            conn.connect();
                            InputStream is = conn.getInputStream();
                            BufferedInputStream bis = new BufferedInputStream(is);
                            bm = BitmapFactory.decodeStream(bis);
                            bis.close();
                            is.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        return bm;
                    }

                    protected void onPostExecute(Bitmap bm) {

                        Drawable drawable = new BitmapDrawable(getResources(), bm);

                        iv_pro_pic.setImageDrawable(drawable);

                    }
                };
                t.execute();
                break;
            case 2:
                Picasso.with(this).load(url.trim()).error(R.drawable.user).into(iv_pro_pic);
                break;
            case 3:
                break;
        }
    }

    public void change_pass(View v) {
        boolean wrapInScrollView = true;
        new MaterialDialog.Builder(this)
                .title("Update your Password")
                .customView(R.layout.change_pass, wrapInScrollView)
                .negativeText("Cancel")
                .positiveText("Done").onPositive(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                String og_pass = (getSharedPreferences("login", Context.MODE_PRIVATE)).getString("pass", "").trim();
                String old_pass = ((EditText) dialog.findViewById(R.id.et_old_pass)).getText().toString().trim();
                String new_pass = ((EditText) dialog.findViewById(R.id.et_new_pass)).getText().toString().trim();
                String new_pass_con = ((EditText) dialog.findViewById(R.id.et_new_pass_con)).getText().toString().trim();

                if (!old_pass.isEmpty() && !new_pass.isEmpty() && !new_pass_con.isEmpty()) {
                    if (new_pass.equals(new_pass_con)) {
                        if (old_pass.equals(og_pass)) {
                            update_details();
                        } else {
                            new SweetAlertDialog(my_profile.this)
                                    .setTitleText("Check old password..!!!")
                                    .show();
                            System.gc();
                        }
                    } else {
                        new SweetAlertDialog(my_profile.this)
                                .setTitleText("Check new password..!!!")
                                .show();
                        System.gc();
                    }
                } else {
                    new SweetAlertDialog(my_profile.this)
                            .setTitleText("Fill the form..!!!")
                            .show();
                    System.gc();
                }


            }
        }).show();

    }

    public void update(View v) {

        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#dfac0e"));
        pDialog.setTitleText("Updating Info....");
        pDialog.setCancelable(false);
        pDialog.show();
        if (changePic) {
            // here upload pic first
            update_details();
        } else
            update_details();
    }

    public String getStringImage(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    public void update_details() {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, new Apis().update,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("mobiplay",response);
                        pDialog.dismiss();
                        switch (response.trim()) {
                            case "fail":
                                new SweetAlertDialog(my_profile.this)
                                        .setTitleText("Not updated..!!!")
                                        .show();
                                System.gc();
                                //  Toast.makeText(login.this, "Not Registered..!!!", Toast.LENGTH_LONG).show();
                                break;
                            case "success":
                                new SweetAlertDialog(my_profile.this)
                                        .setTitleText("Updated..!!!")
                                        .show();
                                System.gc();
                                break;

                            default:
                                break;
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        pDialog.dismiss();
                        new SweetAlertDialog(my_profile.this)
                                .setTitleText("Check Internet Connection")
                                .show();
                        System.gc();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                SharedPreferences view_sharedpreferences = getSharedPreferences("login", Context.MODE_PRIVATE);
                Map<String, String> params = new HashMap<String, String>();
                params.put("first_name", et_first_name.getText().toString().trim());
                params.put("last_name", et_last_name.getText().toString().trim());
                params.put("bdate", tv_dob.getText().toString().trim());
                if (changePic) {
                    ;
                    params.put("image", getStringImage(iv_pro_pic.getBitmap()));
                    params.put("pic_url_type", 1+"");
                    params.put("pic_url", "");
                }
                else {
                    params.put("pic_url_type", 2+"");
                    params.put("pic_url", view_sharedpreferences.getString("profile_pic", ""));
                }
                params.put("account_type", view_sharedpreferences.getInt("account_type", 10) + "");
                params.put("id", view_sharedpreferences.getString("id", ""));
                params.put("pass", view_sharedpreferences.getString("pass", ""));
                //params.put("account_type", "3");
                return params;
            }

        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                (int) TimeUnit.SECONDS.toMillis(20),
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
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

    public void changeProfilePic(View v) {
        SharedPreferences view_sharedpreferences = getSharedPreferences("login", Context.MODE_PRIVATE);
        if (view_sharedpreferences.getInt("account_type", 10) == 3) {
            BottomSheet sheet = new BottomSheet.Builder(my_profile.this).icon(getRoundedBitmap(R.drawable.select_img)).title("Select Profile Picture").sheet(R.menu.select_image_send).listener(new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case R.id.camera:
                            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivityForResult(cameraIntent, 973);
                            break;
                        case R.id.gallery:
                            galleryIntent();
                            break;
                        case R.id.cancel:
                            break;
                    }
                }
            }).build();
            sheet.show();
        }

    }

    private Drawable getRoundedBitmap(int imageId) {
        Bitmap src = BitmapFactory.decodeResource(getResources(), imageId);
        Bitmap dst;
        if (src.getWidth() >= src.getHeight()) {
            dst = Bitmap.createBitmap(src, src.getWidth() / 2 - src.getHeight() / 2, 0, src.getHeight(), src.getHeight());
        } else {
            dst = Bitmap.createBitmap(src, 0, src.getHeight() / 2 - src.getWidth() / 2, src.getWidth(), src.getWidth());
        }
        RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(getResources(), dst);
        roundedBitmapDrawable.setCornerRadius(dst.getWidth() / 2);
        roundedBitmapDrawable.setAntiAlias(true);
        return roundedBitmapDrawable;
    }


    private void galleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select File"), 379);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 379)
                onSelectFromGalleryResult(data);
            if (requestCode == 973) {
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                photo = ThumbnailUtils.extractThumbnail(photo, 25, 25);
                iv_pro_pic.setImageBitmap(photo);
                changePic = true;
            }
        }
    }

    private void onSelectFromGalleryResult(Intent data) {
        Bitmap bm = null;
        if (data != null) {
            try {
                bm = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
                bm = ThumbnailUtils.extractThumbnail(bm, 25, 25);
                iv_pro_pic.setImageBitmap(bm);
                changePic = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }


    public void select_date(View v) {
        Calendar now = Calendar.getInstance();
        DatePickerDialog dpd = DatePickerDialog.newInstance(
                my_profile.this,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );


        dpd.setThemeDark(true);
        dpd.vibrate(false);
        dpd.dismissOnPause(false);
        dpd.showYearPickerFirst(false);
        if (true) {
            dpd.setAccentColor(Color.parseColor("#388E3C"));
        }
        if (true) {
            dpd.setTitle("Select Starting Date");
        }
        dpd.show(getFragmentManager(), "Datepickerdialog");
    }

    @Override
    public void onResume() {
        super.onResume();
        DatePickerDialog dpd = (DatePickerDialog) getFragmentManager().findFragmentByTag("Datepickerdialog");
        if (dpd != null) dpd.setOnDateSetListener(this);
    }

    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute, int second) {
//        String hourString = hourOfDay < 10 ? "0"+hourOfDay : ""+hourOfDay;
//        String minuteString = minute < 10 ? "0"+minute : ""+minute;
//        String secondString = second < 10 ? "0"+second : ""+second;
//        String time = "You picked the following time: "+hourString+"h"+minuteString+"m"+secondString+"s";
//       // timeTextView.setText(time);
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        final Calendar c = Calendar.getInstance();
        int ii = 0;
        Calendar cc = Calendar.getInstance();
        cc.set(Calendar.YEAR, year);
        cc.set(Calendar.MONTH, monthOfYear);
        cc.set(Calendar.DAY_OF_MONTH, dayOfMonth);

        if (cc.before(c)) {
            ii = 1;
        }
        if (ii == 1) {
            String date = year + "/" + (++monthOfYear) + "/" + dayOfMonth;
            tv_dob.setText(date);
            System.gc();
            SharedPreferences.Editor edit_sharedpreferences = getSharedPreferences("login", Context.MODE_PRIVATE).edit();
            edit_sharedpreferences.putString("bdate", date);
            edit_sharedpreferences.commit();
            System.gc();
        } else {
            Toast.makeText(this, "Select Valid Starting Date !", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(my_profile.this, settings.class));
        finish();
    }
}

package com.scripton.in.test;

import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.plus.People;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.google.firebase.iid.FirebaseInstanceId;
import com.scripton.in.test.SQLiteDatabase.galleryList;
import com.scripton.in.test.SQLiteDatabase.galleryVideos;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by Nitin on 5/23/2016.
 */
public class login extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks, ResultCallback<People.LoadPeopleResult> {
    private LoginButton loginButton;
    private CallbackManager callbackManager;
    SweetAlertDialog pDialog;


    // google plus login
    GoogleApiClient google_api_client;
    GoogleApiAvailability google_api_availability;
    private static final int SIGN_IN_CODE = 0;
    private static final int PROFILE_PIC_SIZE = 240;
    private ConnectionResult connection_result;
    private boolean is_intent_inprogress;
    private boolean is_signInBtn_clicked;
    private int request_code;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        buidNewGoogleApiClient();
        setContentView(R.layout.login);

        // dailog bar
        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#dfac0e"));
        pDialog.setTitleText("Signing in....");
        pDialog.setCancelable(false);

        getSupportActionBar().hide();
        final TextView info = (TextView) findViewById(R.id.info);
        loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions(Arrays.asList(
                "public_profile", "email", "user_birthday", "user_friends"));
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(final LoginResult loginResult) {
                info.setText(
                        "User ID: "
                                + loginResult.getAccessToken().getUserId()
                                + "\n" +
                                "Auth Token: "
                                + loginResult.getAccessToken().getToken()
                );
                Log.d("mobi_play","token "+loginResult.getAccessToken().getToken());
                Log.d("mobi_play","UID "+loginResult.getAccessToken().getToken());
                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {

                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                try {
                                    pDialog.show();
                                    //Toast.makeText(login.this, "Welcome, " + object.getString("name"), Toast.LENGTH_SHORT).show();

                                    Log.d("errr", "https://graph.facebook.com/" + object.getString("id") + "/picture?type=large");

                                    String FCMtoken = FirebaseInstanceId.getInstance().getToken();
                                    Log.d("err", "InstanceID token: " + FCMtoken);
                                    go_fb_google(object.getString("first_name"), object.getString("last_name"), object.getString("gender"), object.getString("link"), object.getString("birthday"), "https://graph.facebook.com/" + object.getString("id") + "/picture?type=large".trim(), 1,FCMtoken);


                                    return;
                                } catch (JSONException ex) {
                                    Toast.makeText(login.this, ex.getMessage().toString().trim(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,first_name,last_name,gender,birthday,link"); // request only these parameters
                request.setParameters(parameters);
                request.executeAsync();
//               EXAMPLE OF FACEBOOK REPLAY
//                {
//                    "id": "12345678",
//                        "birthday": "1/1/1950",
//                        "first_name": "Chris",
//                        "gender": "male",
//                        "last_name": "Colm",
//                        "link": "http://www.facebook.com/12345678",
//                        "location": {
//                    "id": "110843418940484",
//                            "name": "Seattle, Washington"
//                },
//                    "locale": "en_US",
//                        "name": "Chris Colm",
//                        "timezone": -8,
//                        "updated_time": "2010-01-01T16:40:43+0000",
//                        "verified": true
//                }

//                SharedPreferences.Editor edit_sharedpreferences = getSharedPreferences("login", Context.MODE_PRIVATE).edit();
//                edit_sharedpreferences.putInt("status", 1);
//                edit_sharedpreferences.putInt("account_type",1);
//                edit_sharedpreferences.commit();
//                startActivity(new Intent(login.this, MainActivity.class));
//                finish();
            }

            @Override
            public void onCancel() {
                info.setText("Login attempt canceled.");
            }

            @Override
            public void onError(FacebookException e) {
                Toast.makeText(login.this, "Login attempt failed.", Toast.LENGTH_SHORT).show();
                info.setText("Login attempt failed.");
            }


        });
    }

    /*
    create and  initialize GoogleApiClient object to use Google Plus Api.
    While initializing the GoogleApiClient object, request the Plus.SCOPE_PLUS_LOGIN scope.
    */


    private void buidNewGoogleApiClient() {

        google_api_client = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API, Plus.PlusOptions.builder().build())
                .addScope(Plus.SCOPE_PLUS_LOGIN)
                .addScope(Plus.SCOPE_PLUS_PROFILE)
                .build();
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

    @Override
    protected void onResume() {
        super.onResume();
        if (google_api_client.isConnected()) {
            google_api_client.connect();
        }
    }
    /*
      Will receive the activity result and check which request we are responding to

     */

    @Override
    public void onConnected(Bundle arg0) {
        is_signInBtn_clicked = false;
        getProfileInformation();

        // Update the UI after signin
        //  changeUI(true);

    }

    /**
     * Fetching user's information name, email, profile pic
     */
    private void getProfileInformation() {
        try {
            if (Plus.PeopleApi.getCurrentPerson(google_api_client) != null) {
                Person currentPerson = Plus.PeopleApi
                        .getCurrentPerson(google_api_client);
                String personName = currentPerson.getDisplayName();
                String personPhotoUrl = currentPerson.getImage().getUrl();
                String personGooglePlusProfile = currentPerson.getUrl();
                String personBdate = "";
                if (currentPerson.hasBirthday()) {
                    personBdate = currentPerson.getBirthday();
                }
                String personGender = "";
                if (currentPerson.hasGender()) {
                    switch (currentPerson.getGender()) {
                        case 0:
                            personGender = "Male";
                            break;
                        case 1:
                            personGender = "Female";
                            break;
                        default:
                            personGender = "";
                            break;
                    }
                }

                String email = Plus.AccountApi.getAccountName(google_api_client);

                Log.d("errr", "Name: " + personName + ", plusProfile: "
                        + personGooglePlusProfile + ", email: " + email
                        + ", Image: " + personPhotoUrl);
                // by default the profile url gives 50x50 px image only
                // we can replace the value with whatever dimension we want by
                // replacing sz=X
                personPhotoUrl = personPhotoUrl.substring(0,
                        personPhotoUrl.length() - 2)
                        + PROFILE_PIC_SIZE;
                //str = "Hello I'm your String";
                String[] splited = personName.trim().split("\\s+");

                String FCMtoken = FirebaseInstanceId.getInstance().getToken();
                Log.d("err", "InstanceID token: " + FCMtoken);
                go_fb_google(splited[0], splited[1], personGender, email, personBdate, personPhotoUrl, 2,FCMtoken);

            } else {
                Toast.makeText(getApplicationContext(),
                        "Person information is null", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(),
                    e.getMessage().toString(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onConnectionSuspended(int arg0) {
        google_api_client.connect();
        // changeUI(false);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == SIGN_IN_CODE) {
            request_code = requestCode;
            if (resultCode != RESULT_OK) {
                is_signInBtn_clicked = false;
                pDialog.dismiss();

            }

            is_intent_inprogress = false;

            if (!google_api_client.isConnecting()) {
                google_api_client.connect();
            }
            return;
        }
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }


    private void gPlusSignIn() {
        if (!google_api_client.isConnecting()) {
            Log.d("user connected", "connected");
            pDialog.show();
            is_signInBtn_clicked = true;
            resolveSignInError();
        }
    }



    /*
      Method to resolve any signin errors
     */

    private void resolveSignInError() {
        if (connection_result.hasResolution()) {
            try {
                is_intent_inprogress = true;
                connection_result.startResolutionForResult(this, SIGN_IN_CODE);
                Log.d("resolve error", "sign in error resolved");
            } catch (IntentSender.SendIntentException e) {
                is_intent_inprogress = false;
                google_api_client.connect();
            }
        }
    }

    /*
      Sign-out from Google+ account
     */


    public void fb_login(View v) {
        loginButton.performClick();
    }

    public void google_plus_click(View v) {
        if (new check_internet().check(this)) {
//        Toast.makeText(this, "start sign process", Toast.LENGTH_SHORT).show();
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.GET_ACCOUNTS) == PackageManager.PERMISSION_GRANTED) {
                gPlusSignIn();
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.GET_ACCOUNTS},
                        2);
            }
        } else {
            SweetAlertDialog ss = new SweetAlertDialog(login.this, SweetAlertDialog.ERROR_TYPE);
            ss.setTitleText("Oops...")
                    .setContentText("Check Internet Connection !").show();
        }
    }


    @Override
    public void onConnectionFailed(ConnectionResult result) {
        if (!result.hasResolution()) {
            google_api_availability.getErrorDialog(this, result.getErrorCode(), request_code).show();
            return;
        }

        if (!is_intent_inprogress) {

            connection_result = result;

            if (is_signInBtn_clicked) {

                resolveSignInError();
            }
        }

    }

    public void register(View v) {
        startActivity(new Intent(login.this, register.class));
    }

    public void login(View v) {
        EditText et_email = (EditText) findViewById(R.id.et_email);
        EditText et_pass = (EditText) findViewById(R.id.et_pass);
        if (new check_internet().check(this)) {
            if (!et_email.getText().toString().equals("") && !et_pass.getText().toString().equals("")) {
                pDialog.show();
                go(et_email.getText().toString().trim(), et_pass.getText().toString().trim());
            } else {
                new SweetAlertDialog(login.this)
                        .setTitleText("Fill all the form..!!")
                        .show();
                System.gc();
            }
        } else {
            SweetAlertDialog ss = new SweetAlertDialog(login.this, SweetAlertDialog.ERROR_TYPE);
            ss.setTitleText("Oops...")
                    .setContentText("Check Internet Connection !").show();
            System.gc();
        }
    }


    void go(final String email, final String pass) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, new Apis().login,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        pDialog.dismiss();
                        switch (response.trim()) {
                            case "no":
                                new SweetAlertDialog(login.this)
                                        .setTitleText("Not Registered..!!!")
                                        .show();
                                System.gc();
                                //  Toast.makeText(login.this, "Not Registered..!!!", Toast.LENGTH_LONG).show();
                                break;
                            default:

                                Log.d("errr", response);
                                //Toast.makeText(login.this, response, Toast.LENGTH_LONG).show();
                                showJSON(response.trim());
                                break;
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        pDialog.dismiss();
                        new SweetAlertDialog(login.this)
                                .setTitleText("Check Internet Connection")
                                .show();
                        System.gc();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("email", email);
                params.put("pass", pass);
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


    private void showJSON(String response) {
        try {
            JSONArray jArray = new JSONArray(response.trim());
            for (int i = 0; i < jArray.length(); i++) {
                JSONObject json_data = jArray.getJSONObject(i);

                SharedPreferences.Editor edit_sharedpreferences = getSharedPreferences("login", Context.MODE_PRIVATE).edit();
                edit_sharedpreferences.putInt("status", 1);
                edit_sharedpreferences.putInt("account_type", Integer.parseInt(json_data.getString("account_type").trim()));
                edit_sharedpreferences.putString("first_name", json_data.getString("first_name").trim());
                edit_sharedpreferences.putString("last_name", json_data.getString("last_name").trim());
                edit_sharedpreferences.putString("gender", json_data.getString("gender").trim());
                edit_sharedpreferences.putString("email", json_data.getString("email").trim());
                edit_sharedpreferences.putString("bdate", json_data.getString("bdate").trim());
                edit_sharedpreferences.putString("profile_pic", json_data.getString("profile_pic").trim());
                edit_sharedpreferences.putString("id", json_data.getString("id").trim());
                edit_sharedpreferences.putString("pass", json_data.getString("pass").trim());
                edit_sharedpreferences.commit();
                System.gc();
                startActivity(new Intent(login.this, ads.class));
                finish();
            }
        } catch (Exception e) {
            Log.d("errr", e.getMessage().toString().trim());
        }
    }


    void go_fb_google(final String first_name, final String last_name, final String gender, final String email, final String bdate, final String pic_url, final int account_type,final String FCMtoken) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, new Apis().register_fb_google,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        pDialog.dismiss();


                        Log.d("errr", response.trim());
                        switch (response.trim()) {
                            case "success":
                                SharedPreferences.Editor edit_sharedpreferences = getSharedPreferences("login", Context.MODE_PRIVATE).edit();
                                edit_sharedpreferences.putInt("status", 1);
                                edit_sharedpreferences.putInt("account_type", account_type);
                                edit_sharedpreferences.putString("first_name", first_name);
                                edit_sharedpreferences.putString("last_name", last_name);
                                edit_sharedpreferences.putString("gender", gender);
                                edit_sharedpreferences.putString("email", email);
                                edit_sharedpreferences.putString("bdate", bdate);
                                edit_sharedpreferences.putString("profile_pic", pic_url.trim());
                                edit_sharedpreferences.putString("id", response.trim());
                                edit_sharedpreferences.commit();
                                System.gc();
                                startActivity(new Intent(login.this, ads.class));
                                finish();
                                break;
                            case "fail":
                                SweetAlertDialog ss = new SweetAlertDialog(login.this, SweetAlertDialog.ERROR_TYPE);
                                ss.setTitleText("Oops...")
                                        .setContentText("Failed to login !").show();
                                System.gc();
                                break;
                            default:
                                Log.d("errr", "default");
                                showJSON(response.trim());
                                break;
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        pDialog.dismiss();
                        new SweetAlertDialog(login.this)
                                .setTitleText("Check Internet Connection")
                                .show();
                        System.gc();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("first_name", first_name);
                params.put("last_name", last_name);
                params.put("gender", gender);
                params.put("email", email);
                params.put("bdate", bdate);
                params.put("pic_url", pic_url);
                params.put("account_type", account_type + "");
                params.put("FCMToken", FCMtoken);
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


    @Override
    public void onResult(People.LoadPeopleResult peopleData) {

    }
}

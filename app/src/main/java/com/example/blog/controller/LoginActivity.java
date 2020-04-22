package com.example.blog.controller;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.blog.MainActivity;
import com.example.blog.R;
import com.example.blog.URLs;
import com.example.blog.controller.tools.volley.AppController;
import com.example.blog.controller.tools.volley.FetchJson;
import com.example.blog.controller.tools.volley.IResult;
import com.example.blog.model.Users;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    CallbackManager callbackManager;
    LoginButton fbLogin;
    EditText username,password;
    URLs baseUrl=new URLs();
    String TAG="login";
    IResult mResultCallback = null;
    FetchJson mVolleyService;
    ProgressDialog progress;

    CheckBox checkbox_turath_login;
    boolean turath=false;
    String fbId,fbName,fbPic;
     String fb_user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        progress = new ProgressDialog(this);
        progress.setTitle(R.string.menu_login);
        progress.setMessage(getResources().getString(R.string.wait));
        progress.setCancelable(false);

        username=findViewById(R.id.user_name);
        password=findViewById(R.id.password);


        checkbox_turath_login=findViewById(R.id.checkbox_turath_login);
        checkbox_turath_login.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                turath=b;
                Log.d(TAG, "onCheckedChanged: "+turath);
            }
        });

        Button login=findViewById(R.id.login);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //make api call
//                hideKeyboard(LoginActivity.this);
                progress.show();
                 makeApiCall(username.getText().toString(), password.getText().toString(),turath);
//
            }
        });

        Button register=findViewById(R.id.register);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(),RegisterActivity.class);
                startActivity(intent);
            }
        });


       fbLogin = findViewById(R.id.login_button);

       Button fbLoginCustomBtn=findViewById(R.id.fb_login);
       fbLoginCustomBtn.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               fbLogin.performClick();
           }
       });

        boolean loggedOut = AccessToken.getCurrentAccessToken() == null;



        fbLogin.setReadPermissions(Arrays.asList("email", "public_profile"));
        callbackManager = CallbackManager.Factory.create();

        fbLogin.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code
                //loginResult.getAccessToken();
                //loginResult.getRecentlyDeniedPermissions()
                //loginResult.getRecentlyGrantedPermissions()
                boolean loggedIn = AccessToken.getCurrentAccessToken() == null;
                Log.d("facebook login", loggedIn + " ??");

                    getUserProfile(AccessToken.getCurrentAccessToken());



            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException exception) {

            }
        });

    }

    private void saveFacebookLogin(String id, String name, String img, String email) {
        Log.d(TAG, "saveFacebookLogin: "+email);
        String url=baseUrl.getUrl(baseUrl.getFbLoginInDb());
        initVolleyCallback();
        mVolleyService =new FetchJson(mResultCallback,getApplicationContext());
        fb_user_id=id;
        Map<String,String> params=new HashMap<>();
        params.put("id",id);
        params.put("name",name);
        params.put("picture",img);
        params.put("email",email);
        JSONObject sendJson=new JSONObject(params);
        mVolleyService.postDataVolley("fb",url,sendJson);

    }


    private void makeApiCall(String email, String password, boolean turath) {
        String url=baseUrl.getUrl(baseUrl.getLogin());
        String reqType="login";
        initVolleyCallback();
        mVolleyService =new FetchJson(mResultCallback,getApplicationContext());
        Map<String,String> params=new HashMap<>();
        params.put("email",email);
        params.put("password",password);
        if(turath){
            params.put("db","1");
            reqType="turath";
        }
        JSONObject sendJson=new JSONObject(params);
        mVolleyService.postDataVolley(reqType,url,sendJson);
//
    }



    private void saveToken() {

        SharedPreferences prefs = getSharedPreferences("profile", Activity.MODE_PRIVATE);

        final String user_id=prefs.getString("user_id","");

        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG+" FCM token", "getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token
                       String token = task.getResult().getToken();
                        Log.d(TAG+" token", token);

                        String url = baseUrl.getUrl(baseUrl.getSaveToken());
                        Map<String, String> params = new HashMap<>();
                        if(!user_id.equals(""))
                             params.put("id", user_id);
                        else
                            params.put("id", fb_user_id);

                        params.put("token", token);

                        Log.e(TAG, "saveToken: "+user_id );

                        JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST,
                                url, new JSONObject(params), new Response.Listener<JSONObject>() {

                            @Override
                            public void onResponse(JSONObject response) {
                                Log.e(TAG, response.toString());

                                try { }

                                catch (Exception e) {
                                    e.printStackTrace();
                                    Log.d(TAG, "onErrorResponse: " + e.getMessage());

                                }


                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                VolleyLog.d(TAG, "Error: " + error.getMessage());


                            }

                        });

                        // Adding request to request queue
                        AppController.getInstance().addToRequestQueue(req);

                    }
                });


    }

    void initVolleyCallback(){
        mResultCallback = new IResult() {
            @Override
            public void notifySuccess(String requestType, JSONObject response) {
                Log.d(TAG, "Volley requester " + requestType);
                Log.d(TAG, "Volley JSON post" + response);
//                Toast.makeText(getContext(),"//"+response,Toast.LENGTH_LONG).show();
                progress.dismiss();
                String msg="";
                try {
                    msg = response.getString("message");
                }catch (Exception e){};
                if(requestType.equals("fb")){

                        if (msg.equals("email exists"))
                        {
                            LoginManager.getInstance().logOut();
                            Toast.makeText(getApplicationContext(),R.string.email_taken,Toast.LENGTH_LONG).show();
                        }
                        else {
                            //add fcm token
                            saveToken();

                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                            finish();
                        }


                }
//                else if(requestType.equals("turath")){
//                    makeApiCall(username.getText().toString(), password.getText().toString(),false);
//                }
                else {
                    if (parsJson(response)) {
                        //add fcm token
                        saveToken();

                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(), R.string.login_credentials_wrong, Toast.LENGTH_LONG).show();

                    }
                }


            }
            @Override
            public void notifySuccessJsonArray(String requestType, JSONArray response) {
                Log.d(TAG, "Volley requester " + requestType);
                Log.d(TAG, "Volley JSON post" + response);


//                Toast.makeText(getContext(),"//"+response,Toast.LENGTH_LONG).show();

            }

            @Override
            public void notifyError(String requestType, VolleyError error) {
                Log.d(TAG, "Volley requester " + requestType);
                Log.d(TAG, "Volley JSON post" + error);
//                Toast.makeText(getApplicationContext(),"noooooooo",Toast.LENGTH_SHORT).show();

                progress.dismiss();

            }
        };
    }


    boolean parsJson(JSONObject response) {

        boolean success = false;

        try {


            String name = response.getString("name");
           String profilePic = response.getString("picture");
           String id = response.getString("id");

//            if(profilePic == null || profilePic.equals("") ||profilePic.equals("http://aqlam.turathalanbiaa.com/aqlam/image/000000.png")||profilePic.equals("student.png")){
//                profilePic=baseUrl.getDefaultProfilePic();
//            }

            SharedPreferences prefs = getSharedPreferences("profile", Activity.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("user_id",id);
            editor.putString("user_name",name);
            editor.putString("profile_pic",profilePic);
            editor.apply();
           success=true;


        } catch (JSONException e) {
            success=false;

        }
        return success;
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void getUserProfile(AccessToken currentAccessToken) {
        GraphRequest request = GraphRequest.newMeRequest(
                currentAccessToken, new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        Log.d("TAG", ""+response);
                        if(response.getError() == null)
                            try {

                                String first_name = object.getString("first_name");
                                String last_name = object.getString("last_name");
                                String email = object.getString("email");
                                String id = object.getString("id");
                                String image_url = "https://graph.facebook.com/" + id + "/picture?type=normal";
//                            Toast.makeText(getApplicationContext(),first_name+" "+email+" , "+id,Toast.LENGTH_LONG).show();
//
//
                                saveFacebookLogin(id,first_name+" "+last_name,image_url,email);


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                    }
                });

        Bundle parameters = new Bundle();
        parameters.putString("fields", "first_name,last_name,email,id");
        request.setParameters(parameters);
        request.executeAsync();


    }
    void hideKeyboard(Activity activity) {
        View view = activity.getCurrentFocus();
        InputMethodManager manager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        assert manager != null && view != null;
        manager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }


}


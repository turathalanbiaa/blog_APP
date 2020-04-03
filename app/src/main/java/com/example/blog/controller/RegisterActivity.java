package com.example.blog.controller;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.example.blog.MainActivity;
import com.example.blog.R;
import com.example.blog.URLs;
import com.example.blog.controller.tools.TextValidator;
import com.example.blog.controller.tools.volley.FetchJson;
import com.example.blog.controller.tools.volley.IResult;
import com.facebook.login.LoginManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {

    EditText username,fullname,password,repeatPassword;
    Button register;
    boolean checkUsername=false,checkFullname=false,checkPassword=false,passwordMatch=false;
    Pattern special;

    URLs baseUrl=new URLs();
    String TAG="login";
    IResult mResultCallback = null;
    FetchJson mVolleyService;

    ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        progress = new ProgressDialog(this);
        progress.setTitle(R.string.menu_register);
        progress.setMessage(getResources().getString(R.string.wait));
        progress.setCancelable(false);

        termsAndConditions();
        username=findViewById(R.id.user_name);
        fullname=findViewById(R.id.public_name);
        password=findViewById(R.id.password);
        repeatPassword=findViewById(R.id.password_repeat);
//        special = Pattern.compile ("[!@#$%&*()_+=|<>?{}\\[\\]~-]");


        username.addTextChangedListener(new TextValidator(username) {
            @Override public void validate(TextView textView, String text) {

               if(text.isEmpty()){
                   username.setError(getString(R.string.empty_field_error));
                   checkUsername=false;
               }
//                else if(!Patterns.EMAIL_ADDRESS.matcher(text).matches()){
//                   username.setError(getString(R.string.email_format_error));
//                   checkUsername=false;
//               }
               else
                   checkUsername=true;
            }
        });
        fullname.addTextChangedListener(new TextValidator(fullname) {
            @Override public void validate(TextView textView, String text) {

                if(text.isEmpty()){
                    fullname.setError(getString(R.string.empty_field_error));
                    checkFullname=false;
                }

                else checkFullname=true;

            }
        });



        password.addTextChangedListener(new TextValidator(password) {
            @Override public void validate(TextView textView, String text) {

//                if(special.matcher(text).find()){
//                    password.setError("field required");
//                    checkPassword=false;
//                }
                if(text.length()<6){
                    password.setError(getString(R.string.min_char_error));
                    passwordMatch=false;
                }
                else if(text.isEmpty()){
                    password.setError(getString(R.string.empty_field_error));
                    passwordMatch=false;
                }
                else passwordMatch=true;

            }
        });

        repeatPassword.addTextChangedListener(new TextValidator(repeatPassword) {
            @Override public void validate(TextView textView, String text) {

                if(! text.equals(password.getText().toString())){
                    Log.d("psw", "validate: "+text+"//"+password.getText());
                    repeatPassword.setError(getString(R.string.password_mismatch_error));
                    checkPassword=false;
                }
                else if(text.isEmpty()){
                    repeatPassword.setError(getString(R.string.empty_field_error));
                    checkPassword=false;
                }
                else checkPassword=true;

            }
        });



        register=findViewById(R.id.registerBtn);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (checkData()) {
                    //send reg request
                    //on success log in and go to main page
                    progress.show();
                    makeApiCall(fullname.getText().toString(),username.getText().toString(),password.getText().toString());
//
                }
                else
                    Toast.makeText(getApplicationContext(),R.string.check_fields_for_error,Toast.LENGTH_LONG).show();

            }

        });


    }


    private Boolean checkData() {
        boolean dataCorrect=false;

        if(checkUsername && checkFullname && checkPassword && passwordMatch)
            dataCorrect=true;


        return dataCorrect;
    }

    void termsAndConditions(){
        final Dialog dialog = new Dialog(this, android.R.style.Theme_Black_NoTitleBar);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.argb(100, 0, 0, 0)));
        dialog.setContentView(R.layout.terms_and_conditions);
        dialog.setCancelable(true);
        dialog.show();

        Button agree= dialog.findViewById(R.id.agree);
        agree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
            }
        });
    }

    private void makeApiCall(String name,String email, String password) {
        String url=baseUrl.getUrl(baseUrl.getRegister());
        initVolleyCallback();
        mVolleyService =new FetchJson(mResultCallback,getApplicationContext());
        Map<String,String> params=new HashMap<>();
        params.put("name",name);
        params.put("email",email);
        params.put("password",password);
        JSONObject sendJson=new JSONObject(params);
        mVolleyService.postDataVolley("GETCALL",url,sendJson);
//
    }
    void initVolleyCallback(){
        mResultCallback = new IResult() {
            @Override
            public void notifySuccess(String requestType, JSONObject response) {
                Log.d(TAG, "Volley requester " + requestType);
                Log.d(TAG, "Volley JSON post" + response);
//                Toast.makeText(getApplicationContext(),"//"+response,Toast.LENGTH_LONG).show();
                String msg=null;
                try{
                    msg=response.getString("message");
                }catch (Exception e){}
                progress.dismiss();

                if(msg != null && msg.equals("email exists"))
                {
                    username.setError(getString(R.string.email_taken));
                    Toast.makeText(getApplicationContext(),R.string.email_taken,Toast.LENGTH_LONG).show();

                }
                else if(parsJson(response)){
                    LoginManager.getInstance().logOut();
                    Intent intent=new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    finish();
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
                progress.dismiss();

            }
        };
    }


    boolean parsJson(JSONObject response) {

        boolean success = false;

        try {


            String name = response.getString("name");
//            String imgStr = response.getString("picture");
            String imgStr="";
            String id = response.getString("id");
            if(imgStr == null || imgStr.equals("") || imgStr.equals("http://aqlam.turathalanbiaa.com/aqlam/image/000000.png")){
                imgStr=baseUrl.getDefaultProfilePic();
            }

            Log.d(TAG, "parsJson: "+success);
            SharedPreferences prefs = getSharedPreferences("profile", Activity.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("user_id",id);
            editor.putString("user_name",name);
            editor.putString("profile_pic",imgStr);
            editor.apply();
            success=true;


        } catch (JSONException e) {
            success=false;
            Log.d(TAG, "parsJson: /e "+e);

        }
        return success;
    }



}

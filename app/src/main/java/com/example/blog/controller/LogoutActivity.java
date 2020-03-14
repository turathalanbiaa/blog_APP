package com.example.blog.controller;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.example.blog.MainActivity;
import com.example.blog.R;
import com.facebook.login.LoginManager;

public class LogoutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logout);

        LoginManager.getInstance().logOut();
        SharedPreferences prefs = getSharedPreferences("profile", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor=prefs.edit();
        editor.clear();
        editor.apply();

        Intent intent =new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();
    }
}

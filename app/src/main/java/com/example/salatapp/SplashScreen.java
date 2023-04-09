package com.example.salatapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

public class SplashScreen extends AppCompatActivity {
    Handler screenHandler = new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

       // handler
       screenHandler.postDelayed(new Runnable() {
           @Override
           public void run() {
            Intent screen = new Intent(SplashScreen.this, MainActivity.class);
            startActivity(screen);
            finish();
           }
       },3000);
    }
}
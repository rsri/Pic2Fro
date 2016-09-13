package com.pic2fro.pic2fro.activities;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.pic2fro.pic2fro.R;
import com.pic2fro.pic2fro.util.Constants;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent openingIntent = new Intent(SplashActivity.this, OpeningActivity.class);
                startActivity(openingIntent);

                finish();
            }
        }, Constants.SPLASH_TIME_OUT);
    }
}

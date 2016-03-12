package com.stoneapp.ourvlemoodle2.activities;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;

import android.os.Handler;
import android.os.Bundle;

import com.stoneapp.ourvlemoodle2.util.MoodleConstants;

import com.stoneapp.ourvlemoodle2.R;

public class SplashActivity extends AppCompatActivity {
    private boolean firstTime; // flag to tell whether user already signed in

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);

        SharedPreferences mPref =
                getSharedPreferences(MoodleConstants.PREFS_STRING, Context.MODE_PRIVATE);

        firstTime = mPref.getBoolean(MoodleConstants.ID_PRESENT, true);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!firstTime) {
                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                    SplashActivity.this.startActivity(intent);
                    SplashActivity.this.finish();
                    overridePendingTransition(R.xml.fade_in, R.xml.fade_out); //fade in transition
                } else {
                    Intent intent = new Intent(SplashActivity.this, SignInActivity.class);
                    SplashActivity.this.startActivity(intent);
                    SplashActivity.this.finish();
                    overridePendingTransition(R.xml.fade_in, R.xml.fade_out);
                }
            }
        }, 2000);
    }
}

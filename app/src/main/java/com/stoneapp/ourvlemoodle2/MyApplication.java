package com.stoneapp.ourvlemoodle2;

import android.support.v7.app.AppCompatDelegate;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.app.Application;

/**
 * Created by matthew on 8/31/2016.
 */
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        ActiveAndroid.initialize(this);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }
}

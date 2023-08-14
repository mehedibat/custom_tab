package com.example.custom_tabs;


import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;


import com.example.custom_tabs.notification.NotificationModule;
import com.example.custom_tabs.utils.AppLogger;

import java.util.Objects;
import java.util.Optional;

import timber.log.Timber;

/**
 * Created by FRabbi on 02-07-2023.
 */
public class MyApp extends MultiDexApplication {

    private static MyApp app;


    private boolean isActivityVisible = true;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        AppLogger.init();
        app = this;
        NotificationModule.initNotificationChannel(this.getApplicationContext());
    }

    public static MyApp getInstance() {
        return app;
    }



    public boolean isActivityVisible() {
        return isActivityVisible;
    }

    public void setActivityVisible(boolean activityVisible) {
        isActivityVisible = activityVisible;
    }




}

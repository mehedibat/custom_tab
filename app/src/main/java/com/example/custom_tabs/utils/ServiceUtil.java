package com.example.custom_tabs.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.content.ContextCompat;

import java.util.Objects;

/**
 * Created by Mehedi on 14-08-2023.
 */
public class ServiceUtil {
    public static final String SERVICE_TAG = "SERVICE_TAG";
    public static final int TIMER_SERVICE_ID = 175;
    public static final String ACTION_START_TIMER_SERVICE = "startTimerService";
    public static final String ACTION_STOP_TIMER_SERVICE = "stopTimerService";
    private ServiceUtil() {
    }

    public static boolean isForegroundServiceRunning(Activity activity,Class<?> cls) {
        try {
            ActivityManager activityManager = (ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE);
            if (Objects.isNull(activityManager)) {
                return false;
            }

            for (ActivityManager.RunningServiceInfo serviceInfo : activityManager.getRunningServices(Integer.MAX_VALUE)) {
                if (cls.getName().equals(serviceInfo.service.getClassName())) {
                    if (serviceInfo.foreground) {
                        return true;
                    }
                }
            }

        } catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    public static void startForegroundService(Activity activity,Class<?> cls) {
        try {
            if (!isForegroundServiceRunning(activity,cls)) {
                Intent intent = new Intent(activity.getApplicationContext(), cls);
                intent.setAction(ACTION_START_TIMER_SERVICE);
                ContextCompat.startForegroundService(activity,intent);
                Log.i(SERVICE_TAG,"Executed service start code for :: "+cls.getSimpleName());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void stopForegroundService(Activity activity,Class<?> cls) {
        try {
            if (isForegroundServiceRunning(activity,cls)) {
                Intent intent = new Intent(activity.getApplicationContext(), cls);
                intent.setAction(ACTION_STOP_TIMER_SERVICE);
                ContextCompat.startForegroundService(activity,intent);
                Log.i(SERVICE_TAG,"Executed service stop code for :: "+cls.getSimpleName());
            }
        }catch (Exception e) {
            e.printStackTrace();
        }

    }
}

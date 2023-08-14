package com.example.custom_tabs.service;

import android.Manifest;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ServiceInfo;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;


import com.example.custom_tabs.MainActivity;
import com.example.custom_tabs.MyApp;
import com.example.custom_tabs.R;
import com.example.custom_tabs.notification.NotificationModule;
import com.example.custom_tabs.utils.ServiceUtil;

import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import timber.log.Timber;


/**
 * Created by FRabbi on 16-07-2023.
 */
public class TimerService extends Service {
    private static final String TAG = TimerService.class.getSimpleName();
    private Timer standTimer;


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (isStart(intent)) {
            standCheckerTimer();
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Timber.tag(ServiceUtil.SERVICE_TAG).d("on-destroy()");
        stopTimer();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void stopTimer() {
        if (standTimer != null) {
            Timber.tag(ServiceUtil.SERVICE_TAG).d("stop timer :: " + standTimer);
            standTimer.cancel();
            standTimer = null;
        }
    }

    private boolean isStart(Intent intent) {
        if (Objects.isNull(intent) || Objects.isNull(intent.getAction())) {
            return false;
        }

        final String action = intent.getAction();

        if (action.equals(ServiceUtil.ACTION_START_TIMER_SERVICE)) {
            startService();
            return true;
        }

        if (action.equals(ServiceUtil.ACTION_STOP_TIMER_SERVICE)) {
            stopService();
        }

        return false;

    }

    private void stopService() {
        stopForeground(true);
        stopTimer();
        stopSelf();
        Timber.tag(ServiceUtil.SERVICE_TAG).i("stop foreground service :: " + TAG);
    }

    private void startService() {
        Intent resultIntent = new Intent(this, MainActivity.class);
        resultIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent pendingIntent = NotificationModule.getActivityPendingIntent(getApplicationContext(), resultIntent);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), NotificationModule.NOTIFICATION_CHANNEL_FOREGROUND);

        builder.setSmallIcon(R.mipmap.ic_launcher)
                .setColor(ContextCompat.getColor(this, android.R.color.transparent))
                .setContentTitle(getString(R.string.app_name))
                .setContentText("Running")
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setContentIntent(pendingIntent)
                .setAutoCancel(false)
                .setPriority(NotificationCompat.PRIORITY_MAX);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            startForeground(ServiceUtil.TIMER_SERVICE_ID, builder.build(),
                    ServiceInfo.FOREGROUND_SERVICE_TYPE_NONE);
        } else {
            startForeground(ServiceUtil.TIMER_SERVICE_ID, builder.build());
        }

        Timber.tag(ServiceUtil.SERVICE_TAG).i("start foreground service :: " + TAG);

    }

    private void standCheckerTimer() {
        Timber.tag(ServiceUtil.SERVICE_TAG).d("standCheckerTimer");
        if (standTimer != null) {
            standTimer.cancel();
            standTimer = null;
        }
        standTimer = new Timer();
        standTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Timber.tag(ServiceUtil.SERVICE_TAG).i("activity-visible status:: %s",MyApp.getInstance().isActivityVisible());
                if (!MyApp.getInstance().isActivityVisible()) {
                    Intent dialogIntent = new Intent(TimerService.this, MainActivity.class);
                    dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    //  dialogIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(dialogIntent);
                    Timber.tag(ServiceUtil.SERVICE_TAG).i("launch-activity");
                }
            }
        }, 0, 2000);
    }

}

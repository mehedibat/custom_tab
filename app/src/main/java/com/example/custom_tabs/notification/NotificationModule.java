package com.example.custom_tabs.notification;

import static android.content.Context.NOTIFICATION_SERVICE;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;

import androidx.core.app.NotificationManagerCompat;

import java.util.Arrays;
import java.util.Objects;

/**
 * Created by Mehedi on 16-07-2023.
 */
public class NotificationModule {
    @SuppressLint("StaticFieldLeak")
    private static NotificationManagerCompat NOTIFICATION_MANAGER_INSTANCE;
    public static final String NOTIFICATION_CHANNEL_FOREGROUND = "NOTIFICATION_FOREGROUND";

    private NotificationModule() {
    }

    public static void initNotificationChannel(Context context) {
        long pattern[] = {0, 1000, 500, 1000};

        final NotificationManager manager =
                (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_FOREGROUND, "Notification foreground",
                    NotificationManager.IMPORTANCE_HIGH);

            notificationChannel.setDescription("");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setVibrationPattern(pattern);
            notificationChannel.enableVibration(true);

            manager.createNotificationChannels(Arrays.asList(notificationChannel));
        }

        // to diaplay notification in DND Mode
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = manager.getNotificationChannel(NOTIFICATION_CHANNEL_FOREGROUND);
            if (Objects.isNull(channel)) {
                return;
            }
            channel.canBypassDnd();
        }
    }

    private static NotificationManagerCompat getNotificationCompatManager(Context context) {
        if (NOTIFICATION_MANAGER_INSTANCE == null) {
            return NOTIFICATION_MANAGER_INSTANCE = NotificationManagerCompat.from(context);
        }
        return NOTIFICATION_MANAGER_INSTANCE;
    }


    @SuppressLint("MissingPermission")
    public static void notifyNotification(Context context, int id, Notification notification) {
        NotificationModule.getNotificationCompatManager(context).notify(id, notification);
    }

    public static PendingIntent getActivityPendingIntent(Context context, Intent activityIntent) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            return PendingIntent.getActivity(context, 0, activityIntent, PendingIntent.FLAG_MUTABLE);
        } else {
            return PendingIntent.getActivity(context, 0, activityIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        }
    }


}

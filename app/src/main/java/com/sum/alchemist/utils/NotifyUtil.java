package com.sum.alchemist.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.NotificationCompat;

import com.sum.alchemist.R;


/**
 * Created by Qiu on 2016/6/1.
 */
public class NotifyUtil {


    public static void showNotify(Context context, String title, String text, int notifyId, Intent intent){
        NotificationManager localNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        PendingIntent localPendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        Notification notification = new NotificationCompat.Builder(context)
                .setContentText(text)
                .setContentTitle(title)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setOngoing(false)
                .setOnlyAlertOnce(true)
                .setWhen(System.currentTimeMillis())
                .setContentIntent(localPendingIntent)
                .setDefaults(NotificationCompat.DEFAULT_SOUND|NotificationCompat.DEFAULT_VIBRATE|NotificationCompat.DEFAULT_LIGHTS)
                .setAutoCancel(true)
                .build();
        localNotificationManager.notify(notifyId, notification);

    }

    public static void hideNotify(Context context, int notifyId){
        NotificationManager localNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        localNotificationManager.cancel(notifyId);
    }
}

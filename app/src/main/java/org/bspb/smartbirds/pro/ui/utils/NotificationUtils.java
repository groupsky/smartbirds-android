package org.bspb.smartbirds.pro.ui.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import org.bspb.smartbirds.pro.R;
import org.bspb.smartbirds.pro.enums.EntryType;
import org.bspb.smartbirds.pro.ui.SplashScreenActivity_;

/**
 * Created by dani on 14-11-14.
 */
public class NotificationUtils {

    public static void showMonitoringNotification(Context context) {
        Intent intent = SplashScreenActivity_.intent(context).get();
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);

        PendingIntent resultPendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new NotificationCompat.Builder(context)
                .setContentTitle(context.getString(R.string.monitoring_notification_title))
                .setContentText(context.getString(R.string.monitoring_notification_content))
                .setSmallIcon(R.drawable.ic_launcher)
                .setOngoing(true)
                .setContentIntent(resultPendingIntent)
                .build();


        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, notification);
    }

    public static void hideMonitoringNotification(Context context) {
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(1);
        notificationManager.cancelAll();
    }
}

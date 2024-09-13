package com.example.firsttry.utilities;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;

import androidx.core.app.NotificationCompat;

public class LocalNotificationsHelper
{
    private Context mContext;
    private NotificationManager mNotificationManager;
    private NotificationCompat.Builder mBuilder;
    private static final String NOTIFICATION_CHANNEL_ID = "10001";

    public LocalNotificationsHelper(Context context) {
        mContext = context;
    }

    private void initManager()
    {
        mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    /**
     * Crea e invia una notifica
     * @param title Il titolo della notifica
     * @param message Il messaggio della notifica
     */
    public void createNotification(
            String title,
            String message)
    {
        initManager();
        setupNotificationChannel();
        buildNotification(title, message);
        sendNotification();
    }

    private void sendNotification()
    {
        mNotificationManager.notify(
                (int) System.currentTimeMillis(),
                mBuilder.build());
    }

    private void buildNotification(
            String title,
            String message)
    {
        mBuilder = new NotificationCompat.Builder(mContext, NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH);
    }

    private void setupNotificationChannel()
    {
        if (!isOreoOrHigher())
        {
            return;
        }

        NotificationChannel notificationChannel = new NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "Notifiche standard",
                NotificationManager.IMPORTANCE_HIGH);

        notificationChannel.setDescription("Canale di notifiche generali");
        notificationChannel.enableLights(true);
        notificationChannel.setLightColor(Color.RED);
        notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
        notificationChannel.enableVibration(true);

        mNotificationManager.createNotificationChannel(notificationChannel);
    }

    private Boolean isOreoOrHigher() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.O;
    }
}

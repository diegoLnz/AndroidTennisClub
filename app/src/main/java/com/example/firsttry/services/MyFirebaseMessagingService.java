package com.example.firsttry.services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.example.firsttry.MainActivity;
import com.example.firsttry.R;
import com.example.firsttry.models.Notification;
import com.example.firsttry.utilities.AccountManager;
import com.example.firsttry.utilities.DateTimeExtensions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.concurrent.CompletableFuture;

public class MyFirebaseMessagingService extends FirebaseMessagingService
{

    private static final String TAG = "MyFirebaseMsgService";
    private static final String CHANNEL_ID = "court_notifications_channel";

    private NotificationManager notificationManager;
    private NotificationCompat.Builder notificationBuilder;
    private PendingIntent pendingIntent;

    @Override
    public void onCreate() {
        super.onCreate();
        initManager();
        createNotificationChannel();
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w(TAG, "getToken failed", task.getException());
                        return;
                    }

                    String token = task.getResult();
                    Log.d(TAG, "Token: " + token);
                    saveTokenInDb(token);
                });
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage)
    {
        if (remoteMessage.getNotification() == null) {
            return;
        }
        String title = remoteMessage.getNotification().getTitle();
        String message = remoteMessage.getNotification().getBody();
        sendNotification(title, message);
    }

    @Override
    public void onNewToken(@NonNull String token)
    {
        super.onNewToken(token);
        saveTokenInDb(token);
    }

    private void sendNotification(
            String title,
            String message)
    {
        setPendingIntent();
        setNotificationBuilder(title, message, pendingIntent);
        saveNotification(title, message)
                .thenAccept(notification -> sendNotify(notification.getId()));
    }

    private void initManager()
    {
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    }

    private void setNotificationBuilder(
            String title,
            String message,
            PendingIntent pendingIntent)
    {
        notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);
    }

    private void setPendingIntent()
    {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        pendingIntent = PendingIntent.getActivity(
                this,
                0,
                intent,
                PendingIntent.FLAG_MUTABLE);
    }

    private void createNotificationChannel()
    {
        if (!isOreoOrHigher())
            return;

        NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                "Court Notifications",
                NotificationManager.IMPORTANCE_HIGH
        );
        channel.enableLights(true);
        channel.enableVibration(true);
        notificationManager.createNotificationChannel(channel);
    }

    private void sendNotify(String id)
    {
        notificationManager.notify(Integer.parseInt(id), notificationBuilder.build());
    }

    private Boolean isOreoOrHigher() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.O;
    }

    private void saveTokenInDb(String token)
    {
        AccountManager.saveFcmToken(token);
    }

    private CompletableFuture<Notification> saveNotification(
            String title,
            String body
    )
    {
        return AccountManager.getCurrentAccount().thenCompose(user
                -> new Notification(
                title,
                body,
                user.getId(),
                DateTimeExtensions.now()).save());
    }
}


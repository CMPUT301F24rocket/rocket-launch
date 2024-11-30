package com.example.rocket_launch;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class NotificationHelper {

    private static final String CHANNEL_ID = "My_Notification_Channel";

    /**
     * Creates a notification channel for devices running API 26+.
     * Call this during app initialization or before showing notifications.
     */
    public static void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Event Notifications",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            channel.setDescription("Notifications for events and updates");
            NotificationManager manager = context.getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    /**
     * Displays a notification using the NotificationCompat library.
     *
     * @param context      The application context.
     * @param title        The notification title.
     * @param message      The notification message.
     * @param notificationID Unique ID for the notification.
     */
    @SuppressLint("MissingPermission")
    public static void showNotification(Context context, String title, String message, int notificationID) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.baseline_notifications_24)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat manager = NotificationManagerCompat.from(context);
        manager.notify(notificationID, builder.build());
    }

    /**
     * Updates the user's notification list in the database.
     *
     * @param androidId The user's unique device ID.
     * @param title     The notification title.
     * @param message   The notification message.
     */
    public static void addNotificationToDatabase(String androidId, String title, String message) {
        UsersDB usersDB = new UsersDB();
        Notification notification = new Notification(
                java.util.UUID.randomUUID().toString(),
                title,
                message
        );
        usersDB.addNotification(androidId, notification);
    }

    public static void sendNotification(String androidId, String title, String message) {
        UsersDB usersDB = new UsersDB();
        Notification notification = new Notification(
                java.util.UUID.randomUUID().toString(),
                title,
                message
        );
        usersDB.addNewNotification(androidId, notification);
    }

}


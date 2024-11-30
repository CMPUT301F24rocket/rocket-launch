package com.example.rocket_launch;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.firestore.CollectionReference;

/**
 * contains helpful functions for sending and creating notifications
 * Author: Griffin
 */
public class NotificationHelper {

    private static final String CHANNEL_ID = "My_Notification_Channel";
    private static final UsersDB usersDB = new UsersDB();

    /**
     * Creates a notification channel for devices running API 26+.
     * Call this during app initialization or before showing notifications.
     * Author: griffin
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
     * Author: Griffin
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
     * Author: Griffin
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

    /**
     * sends a notification to the given user with title and message
     * Authors: Griffin, Kaiden
     * @param androidId
     *  id of user to send to
     * @param title
     *  title of the notification
     * @param message
     *  message for the notification
     */
    public static void sendNotification(String androidId, String title, String message) {
        // get user newNotification collection
        CollectionReference notificationCollectionRef = usersDB.getUsersRef().
                document(androidId).collection("newNotifications");

        // create new notification
        Notification notification = new Notification(
                java.util.UUID.randomUUID().toString(),
                title,
                message
        );

        // send notification to recipient
        notificationCollectionRef.add(notification);
    }

    /**
     * sends a pre-made notificatoin to users
     * Authors: Griffin, Kaiden
     * @param androidId
     *  android ID of user to send to
     * @param notification
     *  notification to send to user
     */
    public static void sendPrefabNotification(String androidId, Notification notification) {
        // get user newNotification collection
        CollectionReference notificationCollectionRef = usersDB.getUsersRef().
                document(androidId).collection("newNotifications");

        // send notification to recipient
        notificationCollectionRef.add(notification);
    }

}


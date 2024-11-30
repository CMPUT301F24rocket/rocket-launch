package com.example.rocket_launch;

import android.content.Context;
import android.util.Log;

import com.google.firebase.firestore.DocumentReference;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * intermediate class between receiving and showing a notification
 */
public class NotificationHandler {
    DocumentReference userDocRef;
    Context context;
    List<Map<String, Object>> receivedNotifications;
    UsersDB usersDB;
    String userId;

    /**
     * initialize local notification handler
     * @param context
     *  the application, particularly mainActivity
     * @param userId
     *  androidId of device
     */
    public NotificationHandler(Context context, String userId) {
        this.context = context;
        this.userId = userId;
        usersDB = new UsersDB();
        userDocRef = usersDB.getUsersRef().document(userId);
        receivedNotifications = new ArrayList<>();

        // start listening for new notifications
        startListener();
    }

    /**
     * start notification listener to respond to new notifications
     */
    private void startListener() {
        userDocRef.addSnapshotListener((documentSnapshot, e) -> {
            if (documentSnapshot != null && documentSnapshot.exists()) {
                User user = documentSnapshot.toObject(User.class);
                if (user != null && user.getNewNotifications() != null) {
                    handleNotifications(user.getNewNotifications());
                }
            }
            else {
                Log.d("FirestoreListener", "No data found.");
            }
        });
    }

    private void handleNotifications(List<Notification> notifications) {
        for (Notification notification : notifications) {
            // send notification
            NotificationHelper.createNotificationChannel(context);
            NotificationHelper.showNotification(context,
                    notification.getTitle(),
                    notification.getMessage(), 2);
            // update databases
            usersDB.addNotification(userId, notification);
            usersDB.removeNewNotification(userId, notification);
        }
    }
}

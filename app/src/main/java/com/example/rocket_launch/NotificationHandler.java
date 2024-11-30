package com.example.rocket_launch;

import android.content.Context;
import android.util.Log;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;
import java.util.Map;

/**
 * intermediate class between receiving and showing a notification
 * Author: Kaiden
 */
public class NotificationHandler {
    CollectionReference notificationCollectionRef;
    Context context;
    List<Map<String, Object>> receivedNotifications;
    UsersDB usersDB;
    String userId;

    /**
     * initialize local notification handler
     * Author: Kaiden
     * @param context
     *  the application, particularly mainActivity
     * @param userId
     *  androidId of device
     */
    public NotificationHandler(Context context, String userId) {
        this.context = context;
        this.userId = userId;
        usersDB = new UsersDB();
        notificationCollectionRef = usersDB.getUsersRef().
                document(userId).collection("newNotifications");

        // start listening for new notifications
        startListener();
    }

    /**
     * start notification listener to respond to new notifications
     * Author: Kaiden
     */
    private void startListener() {
        notificationCollectionRef.addSnapshotListener((querySnapshot, e) -> {
            if (querySnapshot != null) {
                handleNotifications(querySnapshot);
            }
            else {
                Log.d("FirestoreListener", "No data found.");
            }
        });
    }

    /**
     * handles and sends out all new notifications contained in querySnapshot
     * Author: Kaiden
     * @param querySnapshot
     *  query of all new notifications in collection
     */
    private void handleNotifications(QuerySnapshot querySnapshot) {
        for (DocumentSnapshot documentSnapshot : querySnapshot) {
            Notification notification = documentSnapshot.toObject(Notification.class);
            if (notification != null) {
                // send notification
                NotificationHelper.createNotificationChannel(context);
                NotificationHelper.showNotification(context,
                        notification.getTitle(),
                        notification.getMessage(), 2);

                // add to user's notifications
                usersDB.addNotification(userId, notification);
            }
            // remove from newNotifications collection (whether null or no)
            documentSnapshot.getReference().delete();
        }
    }
}

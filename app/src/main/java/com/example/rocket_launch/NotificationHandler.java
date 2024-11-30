package com.example.rocket_launch;

import android.content.Context;
import android.util.Log;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.j256.ormlite.stmt.query.Not;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class NotificationHandler {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentReference userDocRef;
    Context context;
    List<Map<String, Object>> receivedNotifications;

    public NotificationHandler(Context context, String userId) {
        this.context = context;
        UsersDB usersDB = new UsersDB();
        userDocRef = usersDB.getUsersRef().document(userId);
        receivedNotifications = new ArrayList<>();
        startListener();
    }

    private void startListener() {
        NotificationHelper.createNotificationChannel(context);
        userDocRef.addSnapshotListener((snapshot, e) -> {
            if (e != null) {
                Log.w("FirestoreListener", "Listen failed.", e);
                return;
            }

            if (snapshot != null && snapshot.exists()) {

                List<Map<String, Object>> notifications = (List<Map<String, Object>>) snapshot.get("notifications");

                if (notifications != null) {
                    handleNotifications(notifications);
                }
            }
            else {
                Log.d("FirestoreListener", "No data found.");
            }
        });
    }

    private void handleNotifications(List<Map<String, Object>> notifications) {
        for (Map<String, Object> notification : notifications) {
            if (!receivedNotifications.contains(notification)) {
                NotificationHelper.showNotification(context, (String) notification.get("title"), (String) notification.get("message"), 2);
            }
        }
    }
}

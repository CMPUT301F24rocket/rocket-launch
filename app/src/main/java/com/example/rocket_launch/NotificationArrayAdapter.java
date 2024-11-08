package com.example.rocket_launch;

import android.content.Context;
import android.widget.ArrayAdapter;

import java.util.ArrayList;

/**
 * array adapter for notifications
 */
public class NotificationArrayAdapter extends ArrayAdapter<Notification> {
    private ArrayList<Notification> notifications;
    private Context context;

    /**
     * constructor for notification array
     * @param context
     *  context for which to display to
     * @param notifications
     *  list of notifications
     */
    public NotificationArrayAdapter(Context context, ArrayList<Notification> notifications){
        super(context,0, notifications);
        this.notifications = notifications;
        this.context = context;
    }
}

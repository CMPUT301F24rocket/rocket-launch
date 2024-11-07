package com.example.rocket_launch;

import android.content.Context;
import android.widget.ArrayAdapter;

import java.util.ArrayList;

public class NotificationArrayAdapter extends ArrayAdapter<Notification> {
    private ArrayList<Notification> notifications;
    private Context context;

    public NotificationArrayAdapter(Context context, ArrayList<Notification> notifications){
        super(context,0, notifications);
        this.notifications = notifications;
        this.context = context;
    }
}

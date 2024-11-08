package com.example.rocket_launch;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class EventArrayAdapter extends ArrayAdapter<Event> {

    public EventArrayAdapter(Context context, ArrayList<Event> events){
        super(context,0,events);
    }

    public View getView(int position, View convertView, @NonNull ViewGroup parent){
        Event event = getItem(position);

        if (convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.event_list_content, parent, false);
        }

        TextView eventName = convertView.findViewById(R.id.list_event_name);

        assert event != null;
        eventName.setText(event.getName());

        return convertView;
    }
}

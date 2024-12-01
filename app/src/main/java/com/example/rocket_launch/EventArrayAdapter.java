package com.example.rocket_launch;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

/**
 * Array adapter to display a list of events.
 */
public class EventArrayAdapter extends ArrayAdapter<Event> {

    /**
     * Constructor
     *
     * @param context Context of where the fragment is.
     * @param events  Events list to display.
     */
    public EventArrayAdapter(Context context, ArrayList<Event> events) {
        super(context, 0, events);
    }

    /**
     * Get current view.
     *
     * @param position    Position in the array.
     * @param convertView View to convert to.
     * @param parent      Parent display.
     * @return Returns a view to display.
     */
    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        Event event = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.event_list_content, parent, false);
        }

        // Bind views
        ImageView eventImage = convertView.findViewById(R.id.list_event_image);
        TextView eventName = convertView.findViewById(R.id.list_event_name);

        // Set data
        assert event != null;
        eventName.setText(event.getName());

        // Load the image
        if (event.getPosterUrl() != null && !event.getPosterUrl().isEmpty()) {
            Log.d("EventArrayAdapter", "Loading image from URL: " + event.getPosterUrl());
            Glide.with(getContext())
                    .load(event.getPosterUrl())
                    .placeholder(R.drawable.sample_poster)
                    .centerCrop()
                    .into(eventImage);
        } else {
            Log.d("EventArrayAdapter", "No poster URL found, using placeholder.");
            eventImage.setImageResource(R.drawable.sample_poster);
        }

        return convertView;
    }
}

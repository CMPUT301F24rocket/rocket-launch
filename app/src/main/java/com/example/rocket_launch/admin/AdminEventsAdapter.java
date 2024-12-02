package com.example.rocket_launch.admin;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.rocket_launch.Event;
import com.example.rocket_launch.R;
import java.util.List;

/**
 * This adapter handles showing a list of events in the admin section.
 * Author: Pouyan
 */
public class AdminEventsAdapter extends RecyclerView.Adapter<AdminEventsAdapter.ViewHolder> {
    private List<Event> events;

    /**
     * Sets up the adapter with a list of events.
     *
     * @param events List of events to display.
     * Author: Pouyan
     */
    public AdminEventsAdapter(List<Event> events) {
        this.events = events;
    }

    /**
     * Creates a ViewHolder when RecyclerView needs a new one.
     *
     * @param parent The parent ViewGroup.
     * @param viewType The type of the view (not used here).
     * @return A ViewHolder for event items.
     * Author: Pouyan
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the layout for an event item
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_admin_event, parent, false);
        return new ViewHolder(view);
    }

    /**
     * Fills the ViewHolder with data for a specific position in the list.
     *
     * @param holder The ViewHolder to update.
     * @param position Index of the event in the list.
     * Author: Pouyan
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Event event = events.get(position);

        // Handle null or empty fields with default values
        String name = (event.getName() != null && !event.getName().trim().isEmpty()) ? event.getName().trim() : "No name provided";
        String description = (event.getDescription() != null && !event.getDescription().trim().isEmpty()) ? event.getDescription().trim() : "No description provided";

        holder.eventName.setText(name);
        holder.eventDescription.setText(description);

        // Handle long-click deletion
        holder.itemView.setOnLongClickListener(v -> {
            if (onEventDeleteListener != null) {
                onEventDeleteListener.onEventDelete(event);
            }
            return true;
        });
    }

    /**
     * Returns the number of events in the list.
     *
     * @return Number of events.
     * Author: Pouyan
     */
    @Override
    public int getItemCount() {
        return events.size();
    }

    /**
     * Replaces the current event list with a new one and refreshes the RecyclerView.
     *
     * @param newEvents The updated list of events.
     * Author: Pouyan
     */
    public void updateData(List<Event> newEvents) {
        this.events = newEvents;
        notifyDataSetChanged(); // Tells RecyclerView to refresh
    }

    /**
     * Holds the views for each event item in the list.
     * Author: Pouyan
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView eventName, eventDescription;

        /**
         * Connects the UI elements of the item view to this ViewHolder.
         *
         * @param itemView The layout of an individual event item.
         * Author: Pouyan
         */
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // Link the TextViews to their XML counterparts
            eventName = itemView.findViewById(R.id.event_name);
            eventDescription = itemView.findViewById(R.id.event_description);
        }
    }

    /**
     * Interface for handling event deletions via long press.
     */
    public interface OnEventDeleteListener {
        void onEventDelete(Event event);
    }

    private OnEventDeleteListener onEventDeleteListener;

    /**
     * Sets the listener for handling event deletions.
     *
     * @param listener The listener for deletion events.
     */
    public void setOnEventDeleteListener(OnEventDeleteListener listener) {
        this.onEventDeleteListener = listener;
    }
}

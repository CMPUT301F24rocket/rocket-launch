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
 * Adapter for displaying a list of events in the admin panel.
 * Handles setting up each event's details and enabling deletion through a long press.
 * Author: Pouyan
 */
public class AdminEventsAdapter extends RecyclerView.Adapter<AdminEventsAdapter.ViewHolder> {
    private List<Event> events;
    private OnEventDeleteListener onEventDeleteListener;

    /**
     * Initializes the adapter with a list of events.
     *
     * @param events List of events to display in the RecyclerView.
     * Author: Pouyan
     */
    public AdminEventsAdapter(List<Event> events) {
        this.events = events;
    }

    /**
     * Creates a new ViewHolder for an event item when needed.
     *
     * @param parent   The parent ViewGroup.
     * @param viewType The view type (not used in this case).
     * @return A ViewHolder for the event item.
     * Author: Pouyan
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the XML layout for an individual event item
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_admin_event, parent, false);
        return new ViewHolder(view);
    }

    /**
     * Binds the data of an event to the ViewHolder.
     *
     * @param holder   The ViewHolder to bind data to.
     * @param position The position of the event in the list.
     * Author: Pouyan
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Event event = events.get(position);

        // Check for null or empty values and use defaults
        String name = (event.getName() != null && !event.getName().trim().isEmpty()) ? event.getName().trim() : "No name provided";
        String description = (event.getDescription() != null && !event.getDescription().trim().isEmpty()) ? event.getDescription().trim() : "No description provided";

        holder.eventName.setText(name);
        holder.eventDescription.setText(description);

        // Set up a long press listener for deleting an event
        holder.itemView.setOnLongClickListener(v -> {
            if (onEventDeleteListener != null) {
                onEventDeleteListener.onEventDelete(event, holder.getAdapterPosition());
            }
            return true;
        });
    }

    /**
     * Returns the total number of events in the list.
     *
     * @return The size of the events list.
     * Author: Pouyan
     */
    @Override
    public int getItemCount() {
        return events.size();
    }

    /**
     * Updates the list of events and refreshes the RecyclerView.
     *
     * @param newEvents The new list of events.
     * Author: Pouyan
     */
    public void updateData(List<Event> newEvents) {
        this.events = newEvents;
        notifyDataSetChanged(); // Notify RecyclerView that the data has changed
    }

    /**
     * Removes an event from the list and notifies the RecyclerView to update.
     *
     * @param position The position of the event to remove.
     * Author: Pouyan
     */
    public void removeEvent(int position) {
        events.remove(position);
        notifyItemRemoved(position); // Animates the removal
    }

    /**
     * Sets the listener for handling event deletions.
     *
     * @param listener The listener to handle event deletion logic.
     * Author: Pouyan
     */
    public void setOnEventDeleteListener(OnEventDeleteListener listener) {
        this.onEventDeleteListener = listener;
    }

    /**
     * ViewHolder for an event item.
     * Holds references to the event's name and description views.
     * Author: Pouyan
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView eventName, eventDescription;

        /**
         * Initializes the ViewHolder by linking the UI elements.
         *
         * @param itemView The root view of the event item layout.
         * Author: Pouyan
         */
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // Find the TextViews for event name and description
            eventName = itemView.findViewById(R.id.event_name);
            eventDescription = itemView.findViewById(R.id.event_description);
        }
    }

    /**
     * Interface for handling long-press deletion of events.
     * Author: Pouyan
     */
    public interface OnEventDeleteListener {
        /**
         * Called when an event is long-pressed for deletion.
         *
         * @param event    The event to delete.
         * @param position The position of the event in the list.
         * Author: Pouyan
         */
        void onEventDelete(Event event, int position);
    }
}

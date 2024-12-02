package com.example.rocket_launch.admin;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.rocket_launch.R;
import com.example.rocket_launch.User;

import java.util.List;

/**
 * Adapter for displaying a list of facilities in a RecyclerView.
 * Handles data binding, updates, and long-click interactions for the admin panel.
 * Author: Pouyan
 */
public class AdminFacilitiesAdapter extends RecyclerView.Adapter<AdminFacilitiesAdapter.ViewHolder> {
    private List<User> facilities; // List of facilities to display
    private final OnItemLongClickListener listener; // Listener for handling long-click actions

    /**
     * Interface for handling long-click events on facility items.
     * Author: Pouyan
     */
    public interface OnItemLongClickListener {
        /**
         * Triggered when a facility item is long-clicked.
         *
         * @param user     The selected facility (User object).
         * @param position The position of the facility in the list.
         * Author: Pouyan
         */
        void onItemLongClick(User user, int position);
    }

    /**
     * Constructs the adapter with a list of facilities and a long-click listener.
     *
     * @param facilities The list of facilities to display.
     * @param listener   The listener for handling long-click events.
     * Author: Pouyan
     */
    public AdminFacilitiesAdapter(List<User> facilities, OnItemLongClickListener listener) {
        this.facilities = facilities;
        this.listener = listener;
    }

    /**
     * Creates a new ViewHolder when the RecyclerView needs it.
     *
     * @param parent   The parent ViewGroup.
     * @param viewType The type of the view (not used here).
     * @return A new ViewHolder instance.
     * Author: Pouyan
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the layout for a facility item
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_admin_facilities, parent, false);
        return new ViewHolder(view);
    }

    /**
     * Binds data to the ViewHolder for a specific position in the list.
     *
     * @param holder   The ViewHolder to bind data to.
     * @param position The position of the item in the list.
     * Author: Pouyan
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = facilities.get(position);

        // Bind facility name and address to the TextViews
        holder.facilityName.setText(user.getUserFacility());
        holder.facilityAddress.setText(user.getUserFacilityAddress());

        // Set up long-click listener for this item
        holder.itemView.setOnLongClickListener(v -> {
            listener.onItemLongClick(user, position); // Notify listener about the long-click
            return true;
        });
    }

    /**
     * Returns the total number of facility items in the list.
     *
     * @return The number of facilities.
     * Author: Pouyan
     */
    @Override
    public int getItemCount() {
        return facilities.size();
    }

    /**
     * Removes a facility from the list at the specified position.
     *
     * @param position The position of the facility to remove.
     * Author: Pouyan
     */
    public void removeFacility(int position) {
        facilities.remove(position); // Remove the facility from the list
        notifyItemRemoved(position); // Notify the RecyclerView about the removal
    }

    /**
     * Updates the list of facilities with new data and refreshes the RecyclerView.
     *
     * @param newFacilities The updated list of facilities.
     * Author: Pouyan
     */
    public void updateData(List<User> newFacilities) {
        facilities.clear(); // Clear the current list
        facilities.addAll(newFacilities); // Add the new facilities
        notifyDataSetChanged(); // Notify RecyclerView to refresh
    }

    /**
     * ViewHolder class for managing individual facility item views.
     * Author: Pouyan
     */
    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView facilityName, facilityAddress; // TextViews for facility details

        /**
         * Initializes the ViewHolder by linking the TextViews to their XML counterparts.
         *
         * @param itemView The root view of the facility item.
         * Author: Pouyan
         */
        ViewHolder(@NonNull View itemView) {
            super(itemView);
            // Link the facility name and address TextViews
            facilityName = itemView.findViewById(R.id.facility_name);
            facilityAddress = itemView.findViewById(R.id.facility_address);
        }
    }
}

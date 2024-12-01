package com.example.rocket_launch.admin;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rocket_launch.R;
import com.example.rocket_launch.data.Facility;

import java.util.ArrayList;

/**
 * Adapter for displaying a list of facilities in the admin view.
 * Responsible for binding facility data to the RecyclerView.
 * Author: Pouyan
 */
public class AdminFacilitiesAdapter extends RecyclerView.Adapter<AdminFacilitiesAdapter.FacilityViewHolder> {

    private ArrayList<Facility> facilities; // List of facilities to display

    /**
     * Constructor for AdminFacilitiesAdapter.
     *
     * @param facilities The list of facilities to be displayed.
     * Author: Pouyan
     */
    public AdminFacilitiesAdapter(ArrayList<Facility> facilities) {
        this.facilities = facilities;
    }

    /**
     * Inflates the layout for individual facility items.
     *
     * @param parent The parent ViewGroup.
     * @param viewType The view type of the new View.
     * @return A new FacilityViewHolder instance.
     * Author: Pouyan
     */
    @NonNull
    @Override
    public FacilityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_admin_facilities, parent, false);
        return new FacilityViewHolder(view);
    }

    /**
     * Binds data to the ViewHolder at the specified position.
     *
     * @param holder The ViewHolder to bind data to.
     * @param position The position of the item in the list.
     * Author: Pouyan
     */
    @Override
    public void onBindViewHolder(@NonNull FacilityViewHolder holder, int position) {
        Facility facility = facilities.get(position); // Get the facility at the current position
        holder.tvFacilityName.setText(facility.getFacilityName().trim());
        holder.tvFacilityAddress.setText(facility.getFacilityAddress().trim());
    }

    /**
     * Returns the total number of facilities.
     *
     * @return The size of the facilities list.
     * Author: Pouyan
     */
    @Override
    public int getItemCount() {
        return facilities.size();
    }

    /**
     * ViewHolder class for binding facility data to the layout.
     * Author: Pouyan
     */
    public static class FacilityViewHolder extends RecyclerView.ViewHolder {
        TextView tvFacilityName;
        TextView tvFacilityAddress;

        /**
         * Constructor for FacilityViewHolder.
         *
         * @param itemView The view of the individual facility item.
         * Author: Pouyan
         */
        public FacilityViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFacilityName = itemView.findViewById(R.id.facility_name); // Matches XML ID
            tvFacilityAddress = itemView.findViewById(R.id.facility_address);
        }
    }
}

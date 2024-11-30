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

public class AdminFacilitiesAdapter extends RecyclerView.Adapter<AdminFacilitiesAdapter.FacilityViewHolder> {

    private ArrayList<Facility> facilities; // Declare the facilities list

    // Constructor to initialize the facilities list
    public AdminFacilitiesAdapter(ArrayList<Facility> facilities) {
        this.facilities = facilities;
    }

    @NonNull
    @Override
    public FacilityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_admin_facilities, parent, false);
        return new FacilityViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FacilityViewHolder holder, int position) {
        Facility facility = facilities.get(position); // Access the facility at the current position
        holder.tvFacilityName.setText(facility.getFacilityName().trim());
        holder.tvFacilityAddress.setText(facility.getFacilityAddress().trim());
    }

    @Override
    public int getItemCount() {
        return facilities.size(); // Return the size of the facilities list
    }

    // ViewHolder class to bind facility data to the layout
    public static class FacilityViewHolder extends RecyclerView.ViewHolder {
        TextView tvFacilityName;
        TextView tvFacilityAddress;

        public FacilityViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFacilityName = itemView.findViewById(R.id.facility_name); // Ensure these IDs match your XML
            tvFacilityAddress = itemView.findViewById(R.id.facility_address);
        }
    }
}

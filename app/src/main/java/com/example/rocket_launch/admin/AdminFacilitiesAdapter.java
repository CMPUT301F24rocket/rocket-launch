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
 * Adapter for displaying facilities in the admin view.
 * Author: Pouyan
 */
public class AdminFacilitiesAdapter extends RecyclerView.Adapter<AdminFacilitiesAdapter.FacilityViewHolder> {

    private ArrayList<Facility> facilities;
    private OnItemLongClickListener longClickListener;

    public AdminFacilitiesAdapter(ArrayList<Facility> facilities, OnItemLongClickListener longClickListener) {
        this.facilities = facilities;
        this.longClickListener = longClickListener;
    }

    @NonNull
    @Override
    public FacilityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_admin_facilities, parent, false);
        return new FacilityViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FacilityViewHolder holder, int position) {
        Facility facility = facilities.get(position);
        holder.tvFacilityName.setText(facility.getFacilityName());
        holder.tvFacilityAddress.setText(facility.getFacilityAddress());

        holder.itemView.setOnLongClickListener(v -> {
            longClickListener.onItemLongClick(position);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return facilities.size();
    }

    public static class FacilityViewHolder extends RecyclerView.ViewHolder {
        TextView tvFacilityName, tvFacilityAddress;

        public FacilityViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFacilityName = itemView.findViewById(R.id.facility_name);
            tvFacilityAddress = itemView.findViewById(R.id.facility_address);
        }
    }

    public interface OnItemLongClickListener {
        void onItemLongClick(int position);
    }
}

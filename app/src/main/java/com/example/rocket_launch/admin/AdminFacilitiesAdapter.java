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
 * Adapter for displaying facilities in a RecyclerView.
 */
public class AdminFacilitiesAdapter extends RecyclerView.Adapter<AdminFacilitiesAdapter.ViewHolder> {
    private List<User> facilities;
    private final OnItemLongClickListener listener;

    public interface OnItemLongClickListener {
        void onItemLongClick(User user, int position);
    }

    public AdminFacilitiesAdapter(List<User> facilities, OnItemLongClickListener listener) {
        this.facilities = facilities;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_admin_facilities, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = facilities.get(position);
        holder.facilityName.setText(user.getUserFacility());
        holder.facilityAddress.setText(user.getUserFacilityAddress());

        holder.itemView.setOnLongClickListener(v -> {
            listener.onItemLongClick(user, position);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return facilities.size();
    }

    public void removeFacility(int position) {
        facilities.remove(position);
        notifyItemRemoved(position);
    }

    public void updateData(List<User> newFacilities) {
        facilities.clear();
        facilities.addAll(newFacilities);
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView facilityName, facilityAddress;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            facilityName = itemView.findViewById(R.id.facility_name);
            facilityAddress = itemView.findViewById(R.id.facility_address);
        }
    }
}

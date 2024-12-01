package com.example.rocket_launch.admin;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rocket_launch.R;
import com.example.rocket_launch.admin.AdminQRDataFragment.QRCodeItem;

import java.util.List;

/**
 * Adapter for displaying a list of QR code data in the admin view.
 * Used in the AdminQRDataFragment to manage QR codes and their related event IDs.
 * Author: Pouyan
 */
public class AdminQRDataAdapter extends RecyclerView.Adapter<AdminQRDataAdapter.ViewHolder> {
    private List<QRCodeItem> qrCodeItems; // List of QR code items to display

    /**
     * Constructor for AdminQRDataAdapter.
     *
     * @param qrCodeItems List of QR code items to be displayed.
     * Author: Pouyan
     */
    public AdminQRDataAdapter(List<QRCodeItem> qrCodeItems) {
        this.qrCodeItems = qrCodeItems;
    }

    /**
     * Inflates the layout for individual QR code items and creates ViewHolder instances.
     *
     * @param parent The parent ViewGroup.
     * @param viewType The view type of the new View.
     * @return A new ViewHolder instance.
     * Author: Pouyan
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the custom layout for QR code data
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_admin_qrdata, parent, false);
        return new ViewHolder(view);
    }

    /**
     * Binds data to the ViewHolder for a specific position.
     *
     * @param holder The ViewHolder to bind data to.
     * @param position The position of the item in the list.
     * Author: Pouyan
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        QRCodeItem item = qrCodeItems.get(position);

        // Set the QR code and event ID values in the TextViews
        holder.qrCodeTextView.setText(item.getQrCode());
        holder.eventIdTextView.setText(item.getEventId());
    }

    /**
     * Returns the total number of items in the list.
     *
     * @return Number of QR code items.
     * Author: Pouyan
     */
    @Override
    public int getItemCount() {
        return qrCodeItems.size();
    }

    /**
     * ViewHolder class to represent and manage individual QR code items in the RecyclerView.
     * Author: Pouyan
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView qrCodeTextView, eventIdTextView;

        /**
         * Constructor for ViewHolder. Binds UI elements to their respective IDs.
         *
         * @param itemView The view representing a single QR code item.
         * Author: Pouyan
         */
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // Link the TextViews for QR code and event ID
            qrCodeTextView = itemView.findViewById(R.id.qr_code);
            eventIdTextView = itemView.findViewById(R.id.event_id);
        }
    }
}


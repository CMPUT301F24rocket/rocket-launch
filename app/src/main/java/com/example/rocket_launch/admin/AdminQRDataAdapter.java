package com.example.rocket_launch.admin;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rocket_launch.R;

import java.util.List;

/**
 * Adapter for displaying QR code data in the admin panel.
 * Handles binding data and deletion functionality.
 * Author: Pouyan
 */
public class AdminQRDataAdapter extends RecyclerView.Adapter<AdminQRDataAdapter.ViewHolder> {
    private List<AdminQRDataFragment.QRCodeItem> qrCodeItems;
    private OnQRCodeDeleteListener onQRCodeDeleteListener;

    /**
     * Constructor for the adapter.
     *
     * @param qrCodeItems List of QRCodeItem objects to be displayed.
     * Author: Pouyan
     */
    public AdminQRDataAdapter(List<AdminQRDataFragment.QRCodeItem> qrCodeItems) {
        this.qrCodeItems = qrCodeItems;
    }

    /**
     * Inflates the item layout for each QR code and creates a ViewHolder.
     *
     * @param parent   The parent ViewGroup.
     * @param viewType The view type (not used here).
     * @return A ViewHolder containing the inflated view.
     * Author: Pouyan
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the layout for each QR code item
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_admin_qrdata, parent, false);
        return new ViewHolder(view);
    }

    /**
     * Binds data to the ViewHolder at the specified position.
     *
     * @param holder   The ViewHolder to update.
     * @param position The position of the item in the list.
     * Author: Pouyan
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AdminQRDataFragment.QRCodeItem item = qrCodeItems.get(position);

        // Set QR code and event ID text
        holder.qrCodeTextView.setText(item.getQrCode());
        holder.eventIdTextView.setText(item.getEventId());

        // Long-click listener for triggering delete confirmation
        holder.itemView.setOnLongClickListener(v -> {
            if (onQRCodeDeleteListener != null) {
                onQRCodeDeleteListener.onQRCodeDelete(item, position);
            }
            return true; // Indicate the event was handled
        });
    }

    /**
     * Returns the total number of items in the adapter.
     *
     * @return The size of the QR code list.
     * Author: Pouyan
     */
    @Override
    public int getItemCount() {
        return qrCodeItems.size();
    }

    /**
     * Sets the listener for handling QR code deletion events.
     *
     * @param listener The listener to be notified on deletion.
     * Author: Pouyan
     */
    public void setOnQRCodeDeleteListener(OnQRCodeDeleteListener listener) {
        this.onQRCodeDeleteListener = listener;
    }

    /**
     * ViewHolder class to hold views for each QR code item.
     * Author: Pouyan
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView qrCodeTextView, eventIdTextView;

        /**
         * Links the TextViews in the layout to the ViewHolder.
         *
         * @param itemView The layout of the individual QR code item.
         * Author: Pouyan
         */
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            qrCodeTextView = itemView.findViewById(R.id.qr_code);
            eventIdTextView = itemView.findViewById(R.id.event_id);
        }
    }

    /**
     * Interface for handling QR code deletion events.
     * Author: Pouyan
     */
    public interface OnQRCodeDeleteListener {
        /**
         * Called when a QR code is long-pressed for deletion.
         *
         * @param item     The QR code item to delete.
         * @param position The position of the item in the list.
         */
        void onQRCodeDelete(AdminQRDataFragment.QRCodeItem item, int position);
    }
}

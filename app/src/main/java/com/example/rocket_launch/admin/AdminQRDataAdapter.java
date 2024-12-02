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
 */
public class AdminQRDataAdapter extends RecyclerView.Adapter<AdminQRDataAdapter.ViewHolder> {
    private List<AdminQRDataFragment.QRCodeItem> qrCodeItems;
    private OnQRCodeDeleteListener onQRCodeDeleteListener;

    public AdminQRDataAdapter(List<AdminQRDataFragment.QRCodeItem> qrCodeItems) {
        this.qrCodeItems = qrCodeItems;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_admin_qrdata, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AdminQRDataFragment.QRCodeItem item = qrCodeItems.get(position);
        holder.qrCodeTextView.setText(item.getQrCode());
        holder.eventIdTextView.setText(item.getEventId());

        // Long-click to trigger deletion confirmation
        holder.itemView.setOnLongClickListener(v -> {
            if (onQRCodeDeleteListener != null) {
                onQRCodeDeleteListener.onQRCodeDelete(item, position);
            }
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return qrCodeItems.size();
    }

    public void setOnQRCodeDeleteListener(OnQRCodeDeleteListener listener) {
        this.onQRCodeDeleteListener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView qrCodeTextView, eventIdTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            qrCodeTextView = itemView.findViewById(R.id.qr_code);
            eventIdTextView = itemView.findViewById(R.id.event_id);
        }
    }

    public interface OnQRCodeDeleteListener {
        void onQRCodeDelete(AdminQRDataFragment.QRCodeItem item, int position);
    }
}
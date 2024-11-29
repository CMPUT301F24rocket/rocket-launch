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

public class AdminQRDataAdapter extends RecyclerView.Adapter<AdminQRDataAdapter.ViewHolder> {
    private List<QRCodeItem> qrCodeItems;

    public AdminQRDataAdapter(List<QRCodeItem> qrCodeItems) {
        this.qrCodeItems = qrCodeItems;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_admin_qrdata, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        QRCodeItem item = qrCodeItems.get(position);

        holder.qrCodeTextView.setText(item.getQrCode());
        holder.eventIdTextView.setText(item.getEventId());
    }

    @Override
    public int getItemCount() {
        return qrCodeItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView qrCodeTextView, eventIdTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            qrCodeTextView = itemView.findViewById(R.id.qr_code);
            eventIdTextView = itemView.findViewById(R.id.event_id);
        }
    }
}

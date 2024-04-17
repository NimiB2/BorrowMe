package com.project1.borrowme.adapters;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;
import com.project1.borrowme.R;
import com.project1.borrowme.models.Borrow;
import com.project1.borrowme.models.ReceivedBorrow;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {
    private Context context;
    private Map<String, ReceivedBorrow> receivedBorrows;
    private List<ReceivedBorrow> history;

    public HistoryAdapter(Context context, Map<String, ReceivedBorrow> receivedBorrows) {
        this.context = context;
        this.history = new ArrayList<>(receivedBorrows.values());
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.history_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ReceivedBorrow receivedBorrow = getItem(position);
        Borrow borrow = receivedBorrow.getBorrow();

        holder.borrow_item_MTV_item_name.setText(borrow.getItemName());
        String categoriesText = "[" + TextUtils.join(", ", borrow.getCategories()) + "]";
        holder.borrow_item_MTV_categories.setText(categoriesText);
        holder.borrow_item_MTV_radius.setText("" + borrow.getRadiusKm());
        holder.borrow_item_MTV_description.setText(borrow.getDescription());

        if (receivedBorrow.isMe()) {
            holder.title_sender.setVisibility(View.GONE);
            holder.borrow_item_MTV_sender.setVisibility(View.GONE);
            setAddress(holder, borrow);
            if (borrow.isBorrowComplete()) {
                if (borrow.isSucceeded()) {
                    holder.borrow_item_SIV_statusIcon.setImageResource(R.drawable.approve);
                } else {
                    holder.borrow_item_SIV_statusIcon.setImageResource(R.drawable.rejected);
                }
            }else {
                holder.borrow_item_SIV_statusIcon.setImageResource(R.drawable.sand_clock);
            }
        } else {

            holder.borrow_item_MTV_sender.setText(borrow.getSenderName());
            holder.title_address.setVisibility(View.GONE);
            holder.borrow_item_MTV_address.setVisibility(View.GONE);
            if (receivedBorrow.isApprove()) {
                holder.borrow_item_SIV_statusIcon.setImageResource(R.drawable.approve);
            } else {
                holder.borrow_item_SIV_statusIcon.setImageResource(R.drawable.rejected);
            }
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.toggleDetailsVisibility();
            }
        });
    }

    private void setAddress(@NonNull ViewHolder holder, Borrow borrow) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        double latitude = borrow.getLat();
        double longitude = borrow.getLon();
        List<Address> addresses = null;
        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (!addresses.isEmpty()) {
            Address address = addresses.get(0);
            String location = address.getAddressLine(0);
            holder.borrow_item_MTV_address.setText(location);
        } else {
            try {
                throw new IOException("No address found for coordinates: " + latitude + ", " + longitude);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public int getItemCount() {
        return history == null ? 0 : history.size();
    }

    private ReceivedBorrow getItem(int position) {
        return history.get(position);
    }

    @Override
    public void onViewRecycled(@NonNull ViewHolder holder) {
        super.onViewRecycled(holder);
        // Reset the detail layout visibility to gone when the view is recycled
        holder.detailLayout.setVisibility(View.GONE);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        MaterialTextView borrow_item_MTV_item_name, borrow_item_MTV_sender, borrow_item_MTV_categories, borrow_item_MTV_address, borrow_item_MTV_radius, borrow_item_MTV_description, borrow_item_MTV_status, title_sender, title_address;
        ShapeableImageView borrow_item_SIV_statusIcon;
        LinearLayout detailLayout;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            borrow_item_MTV_item_name = itemView.findViewById(R.id.borrow_item_MTV_item_name);
            borrow_item_MTV_sender = itemView.findViewById(R.id.borrow_item_MTV_sender);
            borrow_item_MTV_categories = itemView.findViewById(R.id.borrow_item_MTV_categories);
            borrow_item_MTV_address = itemView.findViewById(R.id.borrow_item_MTV_address);
            borrow_item_MTV_radius = itemView.findViewById(R.id.borrow_item_MTV_radius);
            borrow_item_MTV_description = itemView.findViewById(R.id.borrow_item_MTV_description);
            borrow_item_MTV_status = itemView.findViewById(R.id.borrow_item_MTV_status);
            borrow_item_SIV_statusIcon = itemView.findViewById(R.id.borrow_item_SIV_statusIcon);
            title_sender = itemView.findViewById(R.id.title_sender);
            title_address = itemView.findViewById(R.id.title_address);
            detailLayout = itemView.findViewById(R.id.detail_layout);
            detailLayout.setVisibility(View.GONE);

        }

        // Method to toggle detail visibility
        public void toggleDetailsVisibility() {
            if (detailLayout.getVisibility() == View.GONE) {
                detailLayout.setVisibility(View.VISIBLE);
            } else {
                detailLayout.setVisibility(View.GONE);
            }
        }
    }
}

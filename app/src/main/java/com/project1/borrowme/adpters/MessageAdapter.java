package com.project1.borrowme.adpters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;
import com.project1.borrowme.R;
import com.project1.borrowme.models.ReceivedBorrow;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    private Context context;
    private List<ReceivedBorrow> messages;
    private LayoutInflater inflater;

    public MessageAdapter(Context context, Map<String, ReceivedBorrow> messages) {
        this.context = context;
        this.messages = new ArrayList<>(messages.values());
        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.message_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ReceivedBorrow message = messages.get(position);
        holder.bind(message);
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        MaterialTextView message_item_MTV_item_name, message_item_MTV_user_name, message_item_MTV_description, message_MTV_sender, message_MTV_description;
        ShapeableImageView message_item_IMG_status;
        MaterialButton message_item_BTN_approve, message_item_BTN_reject;
        View layoutButtons;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            message_item_MTV_item_name = itemView.findViewById(R.id.message_item_MTV_item_name);
            message_item_MTV_user_name = itemView.findViewById(R.id.message_item_MTV_user_name);
            message_item_MTV_description = itemView.findViewById(R.id.message_item_MTV_description);
            message_item_IMG_status = itemView.findViewById(R.id.message_item_IMG_status);
            message_item_BTN_approve = itemView.findViewById(R.id.message_item_BTN_approve);
            message_item_BTN_reject = itemView.findViewById(R.id.message_item_BTN_reject);
            message_MTV_sender = itemView.findViewById(R.id.message_MTV_sender);
            message_MTV_description = itemView.findViewById(R.id.message_MTV_description);
        }

        void bind(ReceivedBorrow message) {
            message_item_MTV_item_name.setText(message.getBorrow().getItemName());
            message_MTV_sender.setText(message.getBorrow().getSenderName());
            message_MTV_description.setText(message.getBorrow().getDescription());

            message_item_BTN_approve.setOnClickListener(v -> updateUIForStatus(true, message));
            message_item_BTN_reject.setOnClickListener(v -> updateUIForStatus(false, message));
        }

        private void updateUIForStatus(boolean isApproved, ReceivedBorrow message) {
            // Hide buttons and show the status image
            message_item_BTN_approve.setVisibility(View.GONE);
            message_item_BTN_reject.setVisibility(View.GONE);
            message_item_IMG_status.setVisibility(View.VISIBLE);

            // Set image based on whether approved or rejected
            if (isApproved) {
                message_item_IMG_status.setImageResource(R.drawable.approve);
                message.setApprove(true);
            } else {
                message_item_IMG_status.setImageResource(R.drawable.rejected);
                message.setApprove(false);
            }

            // Optionally, collapse the item to show only the item name and status image
            message_item_MTV_user_name.setVisibility(View.GONE);
            message_item_MTV_description.setVisibility(View.GONE);
            message_MTV_sender.setVisibility(View.GONE);
            message_MTV_description.setVisibility(View.GONE);
        }
    }
}

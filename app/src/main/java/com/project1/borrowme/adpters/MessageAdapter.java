package com.project1.borrowme.adpters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;
import com.project1.borrowme.R;
import com.project1.borrowme.Utilities.FirebaseUtil;
import com.project1.borrowme.interfaces.CallbackAddFirebase;
import com.project1.borrowme.models.ReceivedBorrow;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {
    private Context context;
    private List<ReceivedBorrow> messages;
    private LayoutInflater inflater;


    public MessageAdapter(Context context, List<ReceivedBorrow> messages) {
        this.context = context;
        this.messages = messages;
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
        ReceivedBorrow message = getItem(position);

        configureDateTime(holder, message);
        configureTextFields(holder, message);
        configureButtonListeners(holder, message);
        configureViewVisibility(holder, message);
    }

    private void configureDateTime(ViewHolder holder, ReceivedBorrow message) {
        LocalDateTime localDateTime = message.getCreatedAt().toDate().toInstant()
                .atZone(ZoneId.systemDefault()).toLocalDateTime();
        holder.message_item_MTV_date.setText(localDateTime.format(DateTimeFormatter.ofPattern("dd-MM-yy HH:mm")));
    }

    private void configureTextFields(ViewHolder holder, ReceivedBorrow message) {
        holder.message_item_MTV_item_name.setText(message.getBorrow().getItemName());
        holder.message_MTV_sender.setText(message.getBorrow().getSenderName());
        holder.message_MTV_description.setText(message.getBorrow().getDescription());
    }

    private void configureButtonListeners(ViewHolder holder, ReceivedBorrow message) {
        holder.message_item_BTN_approve.setOnClickListener(v -> updateUIForStatus(holder, true, message));
        holder.message_item_BTN_reject.setOnClickListener(v -> updateUIForStatus(holder, false, message));
        holder.itemView.setOnClickListener(v -> holder.toggleVisibility());
    }

    private void configureViewVisibility(ViewHolder holder, ReceivedBorrow message) {
        if (message.getBorrow().getSenderId().equals(FirebaseUtil.currentUserId())) {
            configureSenderView(holder, message);
        } else {
            configureReceiverView(holder, message);
        }
    }

    private void configureSenderView(ViewHolder holder, ReceivedBorrow message) {
        // Check if the sender has made a final return answer
        if (message.getReturnAnswer()) {
            holder.detail_layout.setVisibility(View.GONE);
            holder.message_item_BTN_approve.setVisibility(View.GONE);
            holder.message_item_BTN_reject.setVisibility(View.GONE);
            holder.message_item_MTV_title.setText(message.getBorrow().getBorrowComplete() ? "There was a deal" : "You rejected the deal");
            holder.message_item_IMG_status.setImageResource(message.getBorrow().getBorrowComplete() ? R.drawable.approve : R.drawable.closed);
        } else {
            holder.message_item_BTN_approve.setVisibility(View.VISIBLE);
            holder.message_item_BTN_reject.setVisibility(View.VISIBLE);
            holder.message_item_MTV_title.setText("Someone approved the request");
            holder.message_item_BTN_approve.setText("Approve Deal");
            holder.message_item_BTN_reject.setText("Reject Deal");
        }
    }

    private void configureReceiverView(ViewHolder holder, ReceivedBorrow message) {
        // Receiver handling based on whether they have responded
        if (message.getAnswer()) {
            holder.detail_layout.setVisibility(View.GONE);
            holder.message_item_BTN_approve.setVisibility(View.GONE);
            holder.message_item_BTN_reject.setVisibility(View.GONE);
            if (message.getApproved()) {
                holder.message_item_MTV_title.setText("Waiting for final approval");
                holder.message_item_IMG_status.setImageResource(R.drawable.approve);
            } else {
                holder.message_item_MTV_title.setText("Borrow Rejected");
                holder.message_item_IMG_status.setImageResource(R.drawable.rejected);
            }
        } else {
            // Allow receiver to respond
            holder.message_item_BTN_approve.setVisibility(View.VISIBLE);
            holder.message_item_BTN_reject.setVisibility(View.VISIBLE);
            holder.message_item_BTN_approve.setText("Approve Borrow");
            holder.message_item_BTN_reject.setText("Reject Borrow");
            holder.message_item_MTV_title.setText("New Borrow Request");
        }
    }


    private void updateUIForStatus(@NonNull ViewHolder holder, boolean isApproved, ReceivedBorrow message) {
        // Always hide these UI elements as a part of updating status
        holder.detail_layout.setVisibility(View.GONE);
        holder.message_item_BTN_reject.setVisibility(View.GONE);
        holder.message_item_BTN_approve.setVisibility(View.GONE);

        if (FirebaseUtil.currentUserId().equals(message.getBorrow().getSenderId())) {
            // This block is executed if the current user is the sender and is responding to the receiver's initial answer.
            message.setReturnAnswer(true);
            String receiverId= message.getReceiveUserId();
            if (isApproved) {
                message.getBorrow().setBorrowComplete(true); // The deal is confirmed/closed.
                holder.message_item_MTV_title.setText("There was a deal");
                holder.message_item_IMG_status.setImageResource(R.drawable.approve);
                updateHistory(message,receiverId);

            } else {
                message.getBorrow().setBorrowComplete(false); // The deal is rejected.
                holder.message_item_MTV_title.setText("The request is closed");
                holder.message_item_IMG_status.setImageResource(R.drawable.closed);
            }
            updateMessages(message,receiverId);
            updateHistory(message,FirebaseUtil.currentUserId());

        } else {
            // This block is executed if the current user is the receiver and is making an initial answer to the sender's request.
            message.getBorrow().updateNumOfAnswers();
            message.setApproved(isApproved);
            message.setAnswer(true);
            String senderId= message.getBorrow().getSenderId();
            if (isApproved) {
                holder.message_item_IMG_status.setImageResource(R.drawable.approve);
                updateMessages(message,senderId);
            } else {
                holder.message_item_IMG_status.setImageResource(R.drawable.rejected);
                updateHistory(message,senderId);
            }
            updateMessages(message,FirebaseUtil.currentUserId());
        }
    }



    private void updateMessages(ReceivedBorrow message, String id) {
        CallbackAddFirebase callbackAddFirebase = new CallbackAddFirebase() {
            @Override
            public void onAddToFirebase(ReceivedBorrow message) {
               updateHistory(message,id);
            }
        };


        FirebaseUtil.addReceivedBorrowToFirestore(message,"Messages",callbackAddFirebase,id);

    }

    private void updateHistory(ReceivedBorrow message, String id) {

        CallbackAddFirebase callbackAddFirebase = new CallbackAddFirebase() {
            @Override
            public void onAddToFirebase(ReceivedBorrow message) {

            }
        };
        FirebaseUtil.addReceivedBorrowToFirestore(message,"history",callbackAddFirebase,id);

    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    private ReceivedBorrow getItem(int position) {
        return messages.get(position);
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        MaterialTextView message_item_MTV_date,message_item_MTV_title, message_item_MTV_item_name, message_item_MTV_user_name, message_item_MTV_description, message_MTV_sender, message_MTV_description;
        ShapeableImageView message_item_IMG_status;
        MaterialButton message_item_BTN_approve, message_item_BTN_reject;
        View layoutButtons;
        LinearLayoutCompat detail_layout;



        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            message_item_MTV_title = itemView.findViewById(R.id.message_item_MTV_title);
            message_item_MTV_item_name = itemView.findViewById(R.id.message_item_MTV_item_name);
            message_item_MTV_user_name = itemView.findViewById(R.id.message_item_MTV_user_name);
            message_item_MTV_description = itemView.findViewById(R.id.message_item_MTV_description);
            message_item_IMG_status = itemView.findViewById(R.id.message_item_IMG_status);
            message_item_BTN_approve = itemView.findViewById(R.id.message_item_BTN_approve);
            message_item_BTN_reject = itemView.findViewById(R.id.message_item_BTN_reject);
            message_MTV_sender = itemView.findViewById(R.id.message_MTV_sender);
            message_MTV_description = itemView.findViewById(R.id.message_MTV_description);
            message_item_MTV_date= itemView.findViewById(R.id.message_item_MTV_date);
            detail_layout = itemView.findViewById(R.id.detail_layout);
        }

        public void toggleVisibility() {
            if (detail_layout.getVisibility() == View.GONE) {
                detail_layout.setVisibility(View.VISIBLE);
            } else {
                detail_layout.setVisibility(View.GONE);
            }
        }
    }
}

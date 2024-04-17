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
import com.project1.borrowme.models.TheUser;

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

        holder.message_item_MTV_item_name.setText(message.getBorrow().getItemName());
        holder.message_MTV_sender.setText(message.getBorrow().getSenderName());
        holder.message_MTV_description.setText(message.getBorrow().getDescription());

        holder.message_item_BTN_approve.setOnClickListener(v -> updateUIForStatus(holder, true, message));
        holder.message_item_BTN_reject.setOnClickListener(v -> updateUIForStatus(holder, false, message));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.toggleVisibility();
            }
        });

        if (message.getBorrow().getSenderId().equals(FirebaseUtil.currentUserId())) {
            if (message.getApproved()) {
                holder.message_item_MTV_title.setText("Borrow Approved");
            } else {
                holder.message_item_MTV_title.setText("Borrow Rejected");
            }

        } else {
            if(message.getAnswer() || message.getBorrow().checkForClosed()){
                holder.message_item_BTN_approve.setVisibility(View.GONE);
                holder.message_item_BTN_reject.setVisibility(View.GONE);

                if (message.getApproved()) {
                    holder.message_item_IMG_status.setImageResource(R.drawable.approve);
                } else {
                    holder.message_item_IMG_status.setImageResource(R.drawable.rejected);
                }
            }
            holder.message_item_MTV_title.setText("New Borrow");
        }

    }

    private void updateUIForStatus(@NonNull ViewHolder holder, boolean isApproved, ReceivedBorrow message) {

        if(message.getAnswer()){
            // return message
            if(isApproved){
                message.getBorrow().setBorrowComplete(true);
                CallbackAddFirebase callbackAddFirebase = new CallbackAddFirebase() {
                    @Override
                    public void onAddToFirebase(ReceivedBorrow message) {

                    }
                };
                FirebaseUtil.addReceivedBorrowToFirestore(message,"history",callbackAddFirebase,message.getReceiveUserId());
            }
            holder.message_item_IMG_status.setImageResource(R.drawable.closed);
        }else{
            // Set image based on whether approved or rejected
            if (isApproved) {
                holder.message_item_IMG_status.setImageResource(R.drawable.approve);
                message.setApproved(true);
            } else {
                holder.message_item_IMG_status.setImageResource(R.drawable.rejected);
                message.setApproved(false);
            }
            message.setAnswer(true);
            message.setApproved(isApproved);

            CallbackAddFirebase callbackAddFirebase = new CallbackAddFirebase() {
                @Override
                public void onAddToFirebase(ReceivedBorrow message) {

                    updateReceived(message);
                }
            };
            FirebaseUtil.addReceivedBorrowToFirestore(message,"Messages",callbackAddFirebase,FirebaseUtil.currentUserId());
        }
        holder.detail_layout.setVisibility(View.GONE);
        holder.message_item_BTN_reject.setVisibility(View.GONE);
        holder.message_item_BTN_approve.setVisibility(View.GONE);
        }

    private void updateReceived(ReceivedBorrow message) {
        CallbackAddFirebase callbackAddFirebase = new CallbackAddFirebase() {
            @Override
            public void onAddToFirebase(ReceivedBorrow message) {

                sendAnswer(message);
            }
        };
        FirebaseUtil.addReceivedBorrowToFirestore(message,"receivedBorrowMap",callbackAddFirebase,FirebaseUtil.currentUserId());

    }

    private void sendAnswer(ReceivedBorrow message) {
        CallbackAddFirebase callbackAddFirebase = new CallbackAddFirebase() {
            @Override
            public void onAddToFirebase(ReceivedBorrow message) {
               message.getBorrow().updateNumOfAnswers();
               updateHistory(message);
            }
        };
        String senderId= message.getBorrow().getSenderId();
        FirebaseUtil.addReceivedBorrowToFirestore(message,"Messages",callbackAddFirebase,senderId);

    }

    private void updateHistory(ReceivedBorrow message) {

        CallbackAddFirebase callbackAddFirebase = new CallbackAddFirebase() {
            @Override
            public void onAddToFirebase(ReceivedBorrow message) {

            }
        };
        String senderId= message.getBorrow().getSenderId();
        FirebaseUtil.addReceivedBorrowToFirestore(message,"history",callbackAddFirebase,senderId);

    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    private ReceivedBorrow getItem(int position) {
        return messages.get(position);
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        MaterialTextView message_item_MTV_title, message_item_MTV_item_name, message_item_MTV_user_name, message_item_MTV_description, message_MTV_sender, message_MTV_description;
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

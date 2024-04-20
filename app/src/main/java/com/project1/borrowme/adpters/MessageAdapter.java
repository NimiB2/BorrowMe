package com.project1.borrowme.adpters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;
import com.project1.borrowme.R;
import com.project1.borrowme.Utilities.FirebaseUtil;
import com.project1.borrowme.interfaces.CallbackAddFirebase;
import com.project1.borrowme.interfaces.CallbackGetFirebase;
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

//        holder.message_item_BTN_reject.setVisibility(View.GONE);
//        holder.message_item_BTN_approve.setVisibility(View.GONE);

        configureDateTime(holder, message);
        configureTextFields(holder, message);
        configureViewVisibility(holder, message);

        holder.itemView.setOnClickListener(v -> {
            holder.toggleVisibility();
            if (holder.detail_layout.getVisibility() == View.VISIBLE) {
                startMes(holder,  message);
            }
        });


    }

    private void startMes(ViewHolder holder, ReceivedBorrow message){
        String theMap = message.getBorrow().getSenderId().equals(FirebaseUtil.currentUserId()) ? "Messages" : "history";
        String theUserId = message.getBorrow().getSenderId().equals(FirebaseUtil.currentUserId()) ? message.getReceiveUserId() : message.getBorrow().getSenderId();
        String borrowId = message.getId();



       CallbackGetFirebase callbackGetFirebase= new CallbackGetFirebase() {
            @Override
            public void onGetFromFirebase(ReceivedBorrow fetchedBorrow) {
                holder.showLoading(false);
                if (fetchedBorrow != null) {
                    holder.message_item_BTN_reject.setVisibility(View.VISIBLE);
                    holder.message_item_BTN_approve.setVisibility(View.VISIBLE);
                    configureButtonListeners(holder, message, fetchedBorrow);

                } else {
                    Log.e("Adapter", "Fetched borrow is null");
                    // Handle case where fetchedBorrow is null if necessary
                }
            }

            @Override
            public void onFailure(Exception e) {
                holder.showLoading(false);
                Log.w("Firestore", "Error fetching borrow details", e);
                // Possibly handle error state in the UI
            }
        };

       boolean me = message.getBorrow().getSenderId().equals(FirebaseUtil.currentUserId());
       if((me&& !(message.getReturnAnswer())) || (!me&& !(message.getAnswer()))) {
           holder.showLoading(true);
           FirebaseUtil.getReceivedBorrowFromFirestore(theMap, theUserId, borrowId, callbackGetFirebase);
       }
       //else{
//           holder.message_item_BTN_reject.setVisibility(View.GONE);
//           holder.message_item_BTN_approve.setVisibility(View.GONE);
     //  }


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

    private void configureButtonListeners(ViewHolder holder, ReceivedBorrow message, ReceivedBorrow fetchedBorrow) {
        holder.message_item_BTN_approve.setOnClickListener(v -> updateUIForStatus(holder, true, message, fetchedBorrow));
        holder.message_item_BTN_reject.setOnClickListener(v -> updateUIForStatus(holder, false, message, fetchedBorrow));

    }

    private void configureViewVisibility(ViewHolder holder, ReceivedBorrow message) {

        if (message.getBorrow().getSenderId().equals(FirebaseUtil.currentUserId())) {
            senderView(holder, message);
        } else {
            receiverView(holder, message);
        }
    }

    private void senderView(ViewHolder holder, ReceivedBorrow message) {


        // Check if the sender has made a final return answer
        if (message.getReturnAnswer()) {
            holder.detail_layout.setVisibility(View.GONE);
//            holder.message_item_BTN_approve.setVisibility(View.GONE);
//            holder.message_item_BTN_reject.setVisibility(View.GONE);

            //fetchedBorrow.getDeal()
            if (message.getDeal()) {
                // If the deal was approved
                holder.message_item_MTV_title.setText("There was a deal");
                holder.message_item_IMG_status.setImageResource(R.drawable.approve);
            } else {
                // If the deal was not approved
                holder.message_item_MTV_title.setText("You rejected the deal");
                holder.message_item_IMG_status.setImageResource(R.drawable.closed);
            }
        } else {
            holder.message_item_MTV_title.setText("Someone approved the request");
            holder.message_item_BTN_approve.setText("Approve Deal");
            holder.message_item_BTN_reject.setText("Reject Deal");
        }


    }

    private void receiverView(ViewHolder holder, ReceivedBorrow message) {
        // Receiver handling based on whether they have responded

        //fetchedBorrow.getBorrow().getOpenBorrow()
        if (!(message.getBorrow().getOpenBorrow()) && !(message.getDeal())) {
//            holder.message_item_BTN_approve.setVisibility(View.GONE);
//            holder.message_item_BTN_reject.setVisibility(View.GONE);
            holder.message_item_MTV_title.setText("Not relevant anymore");
            holder.message_item_IMG_status.setImageResource(R.drawable.closed);
        } else {
            if (message.getAnswer()) {
                holder.detail_layout.setVisibility(View.GONE);
//                holder.message_item_BTN_approve.setVisibility(View.GONE);
//                holder.message_item_BTN_reject.setVisibility(View.GONE);

                if (message.getApproved()) {

                    if (message.getReturnAnswer()) {
                        if (message.getDeal()) {
                            holder.message_item_MTV_title.setText("We hav a deal");
                            holder.message_item_IMG_status.setImageResource(R.drawable.approve);
                        } else {
                            holder.message_item_MTV_title.setText("No deal");
                            holder.message_item_IMG_status.setImageResource(R.drawable.closed);
                        }
                    } else {
                        holder.message_item_MTV_title.setText("Waiting for final approval");
                        holder.message_item_IMG_status.setImageResource(R.drawable.approve);
                    }

                } else {
                    holder.message_item_MTV_title.setText("Borrow Rejected");
                    holder.message_item_IMG_status.setImageResource(R.drawable.rejected);
                }
            } else {
                holder.message_item_BTN_approve.setText("Approve Borrow");
                holder.message_item_BTN_reject.setText("Reject Borrow");
                holder.message_item_MTV_title.setText("New Borrow Request");
            }


        }


    }


    private void updateUIForStatus(@NonNull ViewHolder holder, boolean isApproved, ReceivedBorrow message, ReceivedBorrow fetchedBorrow) {
        // Always hide these UI elements as a part of updating status
        holder.detail_layout.setVisibility(View.GONE);
        holder.message_item_BTN_reject.setVisibility(View.GONE);
        holder.message_item_BTN_approve.setVisibility(View.GONE);


        proceedWithUIUpdate(holder, isApproved, fetchedBorrow, message);


    }

    private void proceedWithUIUpdate(@NonNull ViewHolder holder, boolean isApproved, ReceivedBorrow fetchedBorrow, ReceivedBorrow originalMessage) {

        if (FirebaseUtil.currentUserId().equals(originalMessage.getBorrow().getSenderId())) {
            // The current user is the sender.
            originalMessage.setReturnAnswer(true);
            String receiverId = originalMessage.getReceiveUserId();

            if (isApproved) {
                // sender close the deal
                originalMessage.getBorrow().setBorrowComplete(true); // The deal is confirmed/closed.
                originalMessage.getBorrow().setOpenBorrow(false);
                fetchedBorrow.setDeal(true);
                originalMessage.setDeal(true);
                holder.message_item_MTV_title.setText("There was a deal");
                holder.message_item_IMG_status.setImageResource(R.drawable.approve);
                fetchedBorrow.setId(fetchedBorrow.getBorrow().getId());
                updateHistory(fetchedBorrow, receiverId);

            } else {
                // sender say no for deal
                fetchedBorrow.setDeal(false);
                originalMessage.setDeal(false);
                holder.message_item_MTV_title.setText("The request is closed");
                holder.message_item_IMG_status.setImageResource(R.drawable.closed);
            }
            updateMessages(fetchedBorrow, receiverId);
            updateMessages(originalMessage,FirebaseUtil.currentUserId());

            originalMessage.setId(originalMessage.getBorrow().getId());
            updateHistory(originalMessage, FirebaseUtil.currentUserId());

        } else {
            //The current user is the receiver
            fetchedBorrow.getBorrow().updateNumOfAnswers();
            originalMessage.setApproved(isApproved);
            originalMessage.setAnswer(true);

            String senderId = originalMessage.getBorrow().getSenderId();

            updateMessages(originalMessage, FirebaseUtil.currentUserId());

            if (isApproved) {
                holder.message_item_IMG_status.setImageResource(R.drawable.approve);
                fetchedBorrow.setId(fetchedBorrow.getId()+FirebaseUtil.currentUserId());
                fetchedBorrow.setAnswer(true);
                updateMessages(fetchedBorrow, senderId);
            } else {
                holder.message_item_IMG_status.setImageResource(R.drawable.rejected);
            }

            fetchedBorrow.setAnswer(false);

            fetchedBorrow.setId(fetchedBorrow.getBorrow().getId());


        }


    }


    private void updateMessages(ReceivedBorrow message, String id) {
        CallbackAddFirebase callbackAddFirebase = new CallbackAddFirebase() {
            @Override
            public void onAddToFirebase(ReceivedBorrow message) {

            }
        };


        FirebaseUtil.addReceivedBorrowToFirestore(message, "Messages", callbackAddFirebase, id);

    }

    private void updateHistory(ReceivedBorrow message, String id) {

        CallbackAddFirebase callbackAddFirebase = new CallbackAddFirebase() {
            @Override
            public void onAddToFirebase(ReceivedBorrow message) {

            }
        };

        if(message.getId().equals(message.getBorrow().getId())){
           int x=2;
        }else{
            message.setId(message.getBorrow().getId());
        }


        FirebaseUtil.addReceivedBorrowToFirestore(message, "history", callbackAddFirebase, id);

    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    private ReceivedBorrow getItem(int position) {
        return messages.get(position);
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        MaterialTextView message_item_MTV_date, message_item_MTV_title, message_item_MTV_item_name, message_item_MTV_user_name, message_item_MTV_description, message_MTV_sender, message_MTV_description;
        ShapeableImageView message_item_IMG_status;
        MaterialButton message_item_BTN_approve, message_item_BTN_reject;
        View layoutButtons;
        LinearLayoutCompat detail_layout;
        ProgressBar progressBar;

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
            message_item_MTV_date = itemView.findViewById(R.id.message_item_MTV_date);
            detail_layout = itemView.findViewById(R.id.detail_layout);
            progressBar = itemView.findViewById(R.id.message_item_PB_loading);
            detail_layout.setVisibility(View.GONE);
        }

        void showLoading(boolean show) {
            progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
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
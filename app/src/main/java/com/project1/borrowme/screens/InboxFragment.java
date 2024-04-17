package com.project1.borrowme.screens;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.project1.borrowme.R;
import com.project1.borrowme.adpters.MessageAdapter;
import com.project1.borrowme.models.ReceivedBorrow;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class InboxFragment extends Fragment {
    private TextInputEditText inbox_ET_search;
    private ImageButton inbox_IB_search;
    private RecyclerView inbox_RECYCLER_users_list;
    private MessageAdapter adapter;
    private Map<String, ReceivedBorrow> messages = new HashMap<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_inbox, container, false);

        findViews(view);
        initViews();
        setupRecyclerView();
        loadMessages();

        return view;
    }

    private void findViews(View view) {
        inbox_ET_search = view.findViewById(R.id.inbox_ET_search);
        inbox_IB_search = view.findViewById(R.id.inbox_IB_search);
        inbox_RECYCLER_users_list = view.findViewById(R.id.inbox_RECYCLER_users_list);
    }

    private void initViews() {


        inbox_IB_search.setOnClickListener(v -> performSearch());
        inbox_ET_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // No operation
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // No operation
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().isEmpty()) {
                  //  adapter.updateData(new HashMap<>(messages)); // Reset to full list
                }
            }
        });
    }

    private void performSearch() {
        String searchQuery = inbox_ET_search.getText().toString().trim();
        if (!searchQuery.isEmpty()) {
            Map<String, ReceivedBorrow> filteredMessages = messages.entrySet().stream()
                    .filter(entry -> entry.getValue().getBorrow().getSenderName().contains(searchQuery))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

            if (filteredMessages.isEmpty()) {
                Toast.makeText(getContext(), "No messages found for " + searchQuery, Toast.LENGTH_SHORT).show();
            }
           // adapter.updateData(filteredMessages);
        }
    }

    private void setupRecyclerView() {
        adapter = new MessageAdapter(getContext(), new HashMap<>(messages));
        inbox_RECYCLER_users_list.setLayoutManager(new LinearLayoutManager(getContext()));
        inbox_RECYCLER_users_list.setAdapter(adapter);
    }

    private void loadMessages() {

       // adapter.updateData(messages);
    }
}
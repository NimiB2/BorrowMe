package com.project1.borrowme.screens;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.project1.borrowme.R;
import com.project1.borrowme.Utilities.FirebaseUtil;
import com.project1.borrowme.adpters.HistoryAdapter;
import com.project1.borrowme.models.ReceivedBorrow;
import com.project1.borrowme.models.TheUser;
import com.project1.borrowme.views.NewBorrowingFragment;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class HomeFragment extends Fragment {
    private Map<String, ReceivedBorrow> history;
    private TheUser theUser ;
    private MaterialTextView home_MTV_subTitle;
    private MaterialButton home_BTN_new_borrowing;
    private RecyclerView home_RECYCLER_old_borrowings;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        theUser = TheUser.getInstance();

        findViews(view);
        initViews();
        if (theUser.getUid() != null) {
            updateUI();
        }

        return view;
    }


    private void updateUI() {
        if (!theUser.getHistory().isEmpty()) {
            history = theUser.getHistory();
            FirebaseUtil.currentUserFirestore().collection("history")
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot snapshots,
                                            @Nullable FirebaseFirestoreException e) {
                            if (e != null) {
                                // Handle the error
                                System.err.println("Listen failed: " + e);
                                return;
                            }

//                            history = new HashMap<>();
                            for (DocumentChange dc : snapshots.getDocumentChanges()) {
                                switch (dc.getType()) {
                                    case ADDED:
                                        // Document was added
                                        history.put(dc.getDocument().getId(), dc.getDocument().toObject(ReceivedBorrow.class));
                                        System.out.println("New history: " + dc.getDocument().getData());
                                        break;
                                    case MODIFIED:
                                        // Document was modified
                                        history.put(dc.getDocument().getId(), dc.getDocument().toObject(ReceivedBorrow.class));
                                        System.out.println("Modified history: " + dc.getDocument().getData());
                                        break;
                                    case REMOVED:
                                        // Document was removed
                                        history.remove(dc.getDocument().getId());
                                        System.out.println("Removed history: " + dc.getDocument().getId());
                                        break;
                                }
                            }
                            home_MTV_subTitle.setText(R.string.your_history);
                            setAdapter();
                            // Use or update the history map here as needed
                        }
                    });
//            history = theUser.getHistory();
        } else{
            home_MTV_subTitle.setText(R.string.history);
        }

    }

    private void setAdapter() {
        if (history != null && !history.isEmpty()) {

            ArrayList<ReceivedBorrow> historyArrayList = new ArrayList<>(history.values());
            historyArrayList =(ArrayList<ReceivedBorrow>)historyArrayList.stream()
                    .sorted(Comparator.comparing(ReceivedBorrow::getCreatedAt, Comparator.reverseOrder()))
                    .collect(Collectors.toList());

            HistoryAdapter historyAdapter = new HistoryAdapter(getContext(), historyArrayList);
            home_RECYCLER_old_borrowings.setLayoutManager(new LinearLayoutManager(getContext()));

            home_RECYCLER_old_borrowings.setAdapter(historyAdapter);
            historyAdapter.notifyDataSetChanged();
        }
    }


    private void initViews() {
        home_BTN_new_borrowing.setOnClickListener(v -> {
            Fragment newBorrowingFragment = new NewBorrowingFragment();

            FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.main_FARM_layout, newBorrowingFragment).commit();
        });

    }


    private void findViews(View view) {
        home_MTV_subTitle = view.findViewById(R.id.home_MTV_subTitle);
        home_BTN_new_borrowing = view.findViewById(R.id.home_BTN_new_borrowing);
        home_RECYCLER_old_borrowings = view.findViewById(R.id.home_RECYCLER_old_borrowings);
    }
}
package com.project1.borrowme.screens;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;
import com.project1.borrowme.R;
import com.project1.borrowme.Utilities.UserViewModel;
import com.project1.borrowme.models.ReceivedBorrow;
import com.project1.borrowme.models.TheUser;
import com.project1.borrowme.views.NewBorrowingFragment;

import java.util.Map;

public class HomeFragment extends Fragment {
    private UserViewModel userViewModel;
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
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        userViewModel.getUserLiveData().observe(getViewLifecycleOwner(), theUser -> {
            if (theUser != null) {
                this.theUser = theUser;
                updateUI();
            }
        });


        return view;
    }


    private void updateUI() {
        if (theUser != null && !theUser.getHistory().isEmpty()) {
            history = theUser.getHistory();
            home_MTV_subTitle.setText(R.string.your_history);
            setAdapter();
        } else{
            home_MTV_subTitle.setText(R.string.history);
        }

    }

    private void setAdapter() {
        if (history != null && !history.isEmpty()) {
            com.project1.borrowme.adapters.HistoryAdapter historyAdapter = new com.project1.borrowme.adapters.HistoryAdapter(getContext(), history);
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
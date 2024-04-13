package com.project1.borrowme.screens;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;
import com.project1.borrowme.R;
import com.project1.borrowme.views.NewBorrowingFragment;
import com.project1.borrowme.views.SettingsFragment;

public class HomeFragment extends Fragment {
    MaterialButton home_BTN_new_borrowing;
    RecyclerView home_RECYCLER_old_borrowings;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        findViews(view);
        initViews();
        return view;
    }

    private void initViews() {
        home_BTN_new_borrowing.setOnClickListener(v -> {
            Fragment newBorrowingFragment = new NewBorrowingFragment();

            FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.main_FARM_layout, newBorrowingFragment).commit();
        });
    }

    private void findViews(View view) {
        home_BTN_new_borrowing = view.findViewById(R.id.home_BTN_new_borrowing);
        home_RECYCLER_old_borrowings = view.findViewById(R.id.home_RECYCLER_old_borrowings);
    }
}
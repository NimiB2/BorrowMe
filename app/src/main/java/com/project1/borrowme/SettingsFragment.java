package com.project1.borrowme;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.widget.AppCompatImageButton;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.project1.borrowme.logIns.LoginActivity;
import com.project1.borrowme.views.CategoriesFragment;
import com.project1.borrowme.views.ProfileFragment;

public class SettingsFragment extends Fragment {

    private AppCompatImageButton settings_IMG_back;
    private TextInputEditText settings_ET_username;
    private TextInputEditText settings_ET_email;
    private MaterialButton settings_BTN_changePassword;
    private MaterialButton settings_BTN_manageCategories;
    private MaterialButton settings_BTN_saveChanges;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        findViews(view);
        initViews();
        return view;
    }

    private void initViews() {
        settings_IMG_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backToProfile();
            }
        });


    }

    private void backToProfile() {
            Fragment profileFragment= new ProfileFragment();

            FragmentTransaction fragmentTransaction =getActivity().getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.main_FARM_layout,profileFragment).commit();

    }


    private void findViews(View view) {
        settings_IMG_back = view.findViewById(R.id.settings_IMG_back);
        settings_ET_username = view.findViewById(R.id.settings_ET_username);
        settings_ET_email = view.findViewById(R.id.settings_ET_email);
        settings_BTN_changePassword = view.findViewById(R.id.settings_BTN_changePassword);
        settings_BTN_manageCategories = view.findViewById(R.id.settings_BTN_manageCategories);
        settings_BTN_saveChanges = view.findViewById(R.id.settings_BTN_saveChanges);
    }
}
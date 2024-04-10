package com.project1.borrowme.views;

import android.os.Bundle;

import androidx.appcompat.widget.AppCompatAutoCompleteTextView;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.radiobutton.MaterialRadioButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.project1.borrowme.R;


public class NewBorrowingFragment extends Fragment {
    private TextInputEditText newBorrow_ET_itemName;
    private TextInputLayout newBorrow_TIL_category;
    private AppCompatAutoCompleteTextView newBorrow_ACTV_category;
    private TextInputEditText newBorrow_ET_description;
    private SwitchCompat signUp_SWITCH_location;
    private TextInputEditText signUp_ET_searchBox;
    private MaterialRadioButton radio_distance_1km;
    private MaterialRadioButton radio_distance_5km;
    private MaterialRadioButton radio_distance_10km;
    private MaterialButton newBorrow_BTN_submit;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_new_borrowing, container, false);
        findViews(view);
        initViews();


        return view;
    }

    private void initViews() {
    }

    private void findViews(View view) {
        newBorrow_ET_itemName=view.findViewById(R.id.newBorrow_ET_itemName);
        newBorrow_TIL_category=view.findViewById(R.id.newBorrow_TIL_category);
        newBorrow_ACTV_category=view.findViewById(R.id.newBorrow_ACTV_category);
        newBorrow_ET_description=view.findViewById(R.id.newBorrow_ET_description);
        signUp_SWITCH_location=view.findViewById(R.id.signUp_SWITCH_location);
        signUp_ET_searchBox=view.findViewById(R.id.signUp_ET_searchBox);
        radio_distance_1km=view.findViewById(R.id.radio_distance_1km);
        radio_distance_5km=view.findViewById(R.id.radio_distance_5km);
        radio_distance_10km=view.findViewById(R.id.radio_distance_10km);
        newBorrow_BTN_submit=view.findViewById(R.id.newBorrow_BTN_submit);
    }
}
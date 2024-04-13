package com.project1.borrowme.views;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;

import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.radiobutton.MaterialRadioButton;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;
import com.project1.borrowme.R;
import com.project1.borrowme.Utilities.FirebaseUtil;
import com.project1.borrowme.Utilities.LocationManagerUtil;
import com.project1.borrowme.Utilities.MySignal;
import com.project1.borrowme.interfaces.LocationFetchListener;
import com.project1.borrowme.screens.HomeFragment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class NewBorrowingFragment extends Fragment {
    private AppCompatImageButton inbox_SIV_back;
    private TextInputEditText newBorrow_ET_itemName;
    private TextInputEditText newBorrow_ET_description;
    //    private MaterialButton newBorrow_BTN_category;
//    private ScrollView scroll_categories;
//    private ChipGroup chip_group_categories;
    private SwitchMaterial newBorrow_SWITCH_changeLocation;
    private SwitchCompat newBorrow_SWITCH_getLocation;
    private TextInputEditText newBorrow_FRAGMENT_autoComplete;
    private MaterialRadioButton newBorrow_RADIO_1km;
    private MaterialRadioButton newBorrow_RADIO_5km;
    private MaterialRadioButton newBorrow_RADIO_10km;
    private MaterialButton newBorrow_BTN_submit;

    private LocationManagerUtil locationManagerUtil;
    private FusedLocationProviderClient fusedLocationClient;

    private double lat;
    private double lon;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_new_borrowing, container, false);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(view.getContext());

        findViews(view);
        initViews();
        setupLocationManager();

        return view;
    }

    private void setupLocationManager() {
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)getChildFragmentManager().findFragmentById(R.id.newBorrow_FRAGMENT_autoComplete);
        locationManagerUtil = new LocationManagerUtil(getContext(), autocompleteFragment, new LocationFetchListener() {
            @Override
            public void onLocationFetched(double lat, double lon) {
                setAddress( lat, lon);
            }

            @Override
            public void onLocationFetchFailed() {
                MySignal.getInstance().toast("Failed to fetch location. Please ensure your location services are enabled, or enter a valid address.");
            }
        });
    }

    private void setAddress(double latitude,double longitude)  {
//        Geocoder geocoder = new Geocoder(requireContext(), Locale.getDefault());
//        List<Address> addresses = null;
//        try {
//            addresses = geocoder.getFromLocation(latitude, longitude, 1);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//        if (!addresses.isEmpty()) {
//            Address address = addresses.get(0);
//            String location = address.getAddressLine(0);
//
//        } else {
//            try {
//                throw new IOException("No address found for coordinates: " + latitude + ", " + longitude);
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
//        }
    }

    private void submitForm() {
        if (validateInputs()) {
            MySignal.getInstance().toast("Your request has been successfully sent, we will notify you when there are answers");
        }
    }

    private boolean validateInputs() {
        // Validate item name length
        if (newBorrow_ET_itemName.getText().toString().trim().length() < 2) {
            MySignal.getInstance().toast("Item name must be at least 2 characters.");
            return false;
        }

        // Validate that at least one category is selected
//        if (chip_group_categories.getCheckedChipIds().isEmpty()) {
//            MySignal.getInstance().toast("Please select at least one category.");
//            return false;
//        }

        return true;
    }

    private void changeLocation() {
        if (newBorrow_SWITCH_getLocation.isChecked()) {
            // Get current location from GPS
            locationManagerUtil.getCurrentLocation();
        } else {
            // Use the selected location from autocomplete
                locationManagerUtil.triggerLocationUpdate();
        }

    }
    private void toggleLocationVisibility() {
        if (newBorrow_SWITCH_changeLocation.isChecked()) {
            newBorrow_ET_itemName.setVisibility(View.GONE);
            newBorrow_ET_description.setVisibility(View.GONE);
            newBorrow_SWITCH_getLocation.setVisibility(View.VISIBLE);

            toggleLocationSwitchVisibility();
        }else{
            newBorrow_ET_itemName.setVisibility(View.VISIBLE);
            newBorrow_ET_description.setVisibility(View.VISIBLE);
            newBorrow_SWITCH_getLocation.setVisibility(View.GONE);

        }
    }

    private void toggleLocationSwitchVisibility() {
        if(newBorrow_SWITCH_getLocation.isChecked()){
            newBorrow_FRAGMENT_autoComplete.setVisibility(View.GONE);
        }else{
            newBorrow_FRAGMENT_autoComplete.setVisibility(View.VISIBLE);

        }
    }

    private void backToHome() {
        Fragment homeFragment = new HomeFragment();

        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_FARM_layout, homeFragment).commit();

    }

    private void initViews() {
        inbox_SIV_back.setOnClickListener(v -> backToHome());
        newBorrow_BTN_submit.setOnClickListener(v -> submitForm());
        newBorrow_SWITCH_getLocation.setOnClickListener(v -> changeLocation());
        newBorrow_SWITCH_changeLocation.setOnClickListener(v -> toggleLocationVisibility());
        //  newBorrow_BTN_category.setOnClickListener(v -> showCategoryDialog());
    }

    private void findViews(View view) {
        inbox_SIV_back = view.findViewById(R.id.inbox_SIV_back);
        newBorrow_ET_itemName = view.findViewById(R.id.newBorrow_ET_itemName);
        newBorrow_ET_description = view.findViewById(R.id.newBorrow_ET_description);
        //newBorrow_BTN_category = view.findViewById(R.id.newBorrow_BTN_category);
        // scroll_categories = view.findViewById(R.id.scroll_categories);
        // chip_group_categories = view.findViewById(R.id.chip_group_categories);
        newBorrow_SWITCH_changeLocation = view.findViewById(R.id.newBorrow_SWITCH_changeLocation);
        newBorrow_SWITCH_getLocation = view.findViewById(R.id.newBorrow_SWITCH_getLocation);
        newBorrow_FRAGMENT_autoComplete = view.findViewById(R.id.newBorrow_FRAGMENT_autoComplete);
        newBorrow_RADIO_1km = view.findViewById(R.id.newBorrow_RADIO_1km);
        newBorrow_RADIO_5km = view.findViewById(R.id.newBorrow_RADIO_5km);
        newBorrow_RADIO_10km = view.findViewById(R.id.newBorrow_RADIO_10km);
        newBorrow_BTN_submit = view.findViewById(R.id.newBorrow_BTN_submit);
    }

}
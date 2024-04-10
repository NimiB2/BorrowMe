package com.project1.borrowme;

import android.location.Address;
import android.os.Bundle;

import androidx.appcompat.widget.AppCompatImageButton;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.FrameLayout;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.project1.borrowme.Utilities.FirebaseUtil;
import com.project1.borrowme.Utilities.MySignal;
import com.project1.borrowme.data.CategoriesData;
import com.project1.borrowme.interfaces.CategorySelectionListener;
import com.project1.borrowme.models.Category;
import com.project1.borrowme.models.MyUser;
import com.project1.borrowme.views.CategoriesFragment;
import com.project1.borrowme.views.ProfileFragment;

import java.util.HashMap;
import java.util.Map;

public class SettingsFragment extends Fragment {
    private boolean passwordIsClicked = false;
    private boolean categoriesListIsClicked = false;
    private boolean LocationClicked = false;
    private MyUser myUser;
    private Map<String, Category> categories;
    private Map<String, Category> selectedCategories;
    private AppCompatImageButton settings_IMG_back;
    private TextInputEditText settings_ET_username;
    private TextInputEditText settings_ET_email;
    private MaterialButton settings_BTN_saveChanges;
    private MaterialButton settings_BTN_showChangePassword;
    private TextInputEditText settings_ET_newPassword;
    private MaterialButton settings_BTN_saveNewPassword;
    private MaterialButton settings_BTN_manageCategories;
    private MaterialButton settings_BTN_saveNewCategories;
    private FrameLayout settings_fragment_container;

    private MaterialButton settings_BTN_showChangeLocation;
    private SwitchMaterial settings_SWITCH_location;
    private TextInputEditText settings_ET_address_search;
    private MaterialButton settings_BTN_saveNewLocation;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        findViews(view);
        initViews();
        initCategories();
        initFragment();

        return view;
    }

    private void initCategories() {
        categories = new HashMap<>();
        categories = CategoriesData.getCategories();

        synchronizeCategorySelection();
    }

    private void synchronizeCategorySelection() {
        // Check if both maps are initialized
        if (categories == null || myUser.getCategories() == null) {
            return;
        }
        for (String key : myUser.getCategories().keySet()) {
            // If the category exists in the main categories collection, set it as clicked
            if (categories.containsKey(key)) {
                Category category = categories.get(key);
                category.setClicked(true);
            }
        }
    }

    private void updateCategories(Map<String, Category> selectedCategories) {
        this.selectedCategories = selectedCategories;
    }

    private void initFragment() {
        FragmentManager fragmentManager = getChildFragmentManager();
        CategoriesFragment categoriesFragment = new CategoriesFragment();


        if (categories != null) {
            categoriesFragment.initCategories(categories);
        }
        CategorySelectionListener listener = new CategorySelectionListener() {
            @Override
            public void onCategorySelectionUpdated(Map<String, Category> selectedCategories) {
                updateCategories(selectedCategories);

            }
        };
        categoriesFragment.setSelectionListener(listener);
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.settings_fragment_container, categoriesFragment);
        fragmentTransaction.commit();
    }


    private void initViews() {
        myUser = MyUser.getInstance();
        settings_IMG_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backToProfile();
            }
        });
        settings_ET_username.setText(myUser.getuName());
        settings_ET_email.setText(myUser.getuEmail());

        settings_BTN_saveChanges.setOnClickListener(v -> saveUserDetails());

        settings_BTN_showChangePassword.setOnClickListener(v -> togglePasswordVisibility());
        settings_BTN_saveNewPassword.setOnClickListener(v -> changePassword());

        settings_BTN_showChangeLocation.setOnClickListener(v -> toggleLocationVisibility());
        settings_BTN_saveNewLocation.setOnClickListener(v -> changeLocation());

        settings_BTN_manageCategories.setOnClickListener(v -> toggleCategoriesListVisibility());

        settings_BTN_saveNewCategories.setOnClickListener(v -> changeCategories());
    }

    private void changeCategories() {
        if (selectedCategories != null && !selectedCategories.isEmpty()) {
            myUser.setCategories(selectedCategories);
            MySignal.getInstance().toast("Categories updated successfully!");
        } else {
            MySignal.getInstance().toast("You must select at least 1 categories.");
        }
    }

    private void changeLocation() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid(); // Ensure you have a valid user ID

        if (settings_SWITCH_location.isChecked()) {
            // Get current location and update
            FirebaseUtil.fetchCurrentLocation(getActivity(), location -> {
                if (location != null) {
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();
                    FirebaseUtil.updateUserLocation(userId, latitude, longitude);
                    MySignal.getInstance().toast("Location updated successfully!");
                } else {
                    MySignal.getInstance().toast("Failed to get current location.");
                }
            });
        } else {
            // Get location from address and update
            String addressInput = settings_ET_address_search.getText().toString().trim();
            Address address = FirebaseUtil.fetchLocationFromAddress(getActivity(), addressInput);
            if (address != null) {
                FirebaseUtil.updateUserLocation(userId, address.getLatitude(), address.getLongitude());
                MySignal.getInstance().toast("Location updated successfully!");
            } else {
                MySignal.getInstance().toast("Address not found.");
            }
        }
    }

    private void toggleLocationVisibility() {
        LocationClicked = !LocationClicked;

        if (LocationClicked) {
            settings_SWITCH_location.setVisibility(View.VISIBLE);
            settings_ET_address_search.setVisibility(View.VISIBLE);
            settings_BTN_saveNewLocation.setVisibility(View.VISIBLE);
            toggleSwitchVisibility();

        } else {
            settings_SWITCH_location.setVisibility(View.GONE);
            settings_ET_address_search.setVisibility(View.GONE);
            settings_BTN_saveNewLocation.setVisibility(View.GONE);
        }

    }

    private void toggleSwitchVisibility() {
        settings_SWITCH_location.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // The switch is enabled/checked
                    settings_ET_address_search.setVisibility(View.GONE);
                } else{
                        // The switch is disabled/unchecked
                    settings_ET_address_search.setVisibility(View.VISIBLE);
                    }
                }
            });
        }


        private void togglePasswordVisibility () {
            passwordIsClicked = !passwordIsClicked;

            settings_ET_newPassword.setVisibility(passwordIsClicked ? View.VISIBLE : View.GONE);
            settings_BTN_saveNewPassword.setVisibility(passwordIsClicked ? View.VISIBLE : View.GONE);

        }

        private void toggleCategoriesListVisibility () {
            categoriesListIsClicked = !categoriesListIsClicked;

            settings_fragment_container.setVisibility(categoriesListIsClicked ? View.VISIBLE : View.GONE);
            settings_BTN_saveNewCategories.setVisibility(categoriesListIsClicked ? View.VISIBLE : View.GONE);

            if (categoriesListIsClicked) {
                settings_ET_username.setVisibility(View.GONE);
                settings_BTN_saveChanges.setVisibility(View.GONE);
                settings_BTN_showChangePassword.setVisibility(View.GONE);
                settings_ET_newPassword.setVisibility(View.GONE);
                settings_BTN_saveNewPassword.setVisibility(View.GONE);
                settings_BTN_showChangeLocation.setVisibility(View.GONE);
                settings_SWITCH_location.setVisibility(View.GONE);
                settings_ET_address_search.setVisibility(View.GONE);
                settings_BTN_saveNewLocation.setVisibility(View.GONE);
            } else {
                settings_ET_username.setVisibility(View.VISIBLE);
                settings_BTN_saveChanges.setVisibility(View.VISIBLE);
                settings_BTN_showChangePassword.setVisibility(View.VISIBLE);
                settings_BTN_showChangeLocation.setVisibility(View.VISIBLE);
            }
        }

        private void changePassword () {
            if (FirebaseUtil.validatePassword(settings_ET_newPassword)) {
                String newPassword = settings_ET_newPassword.getText().toString().trim();

                // Update Firebase Auth Password
                FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                if (firebaseUser != null) {
                    firebaseUser.updatePassword(newPassword)
                            .addOnSuccessListener(aVoid -> {
                                MySignal.getInstance().toast("Password updated successfully");
                            })
                            .addOnFailureListener(e -> {
                                MySignal.getInstance().toast("Password updated failed");
                            });
                }
            }
        }

        private void saveUserDetails () {
            if (FirebaseUtil.validateUserName(settings_ET_username) && FirebaseUtil.validateEmail(settings_ET_email)) {
                String userName = settings_ET_username.getText().toString().trim();
                String userEmail = settings_ET_email.getText().toString().trim();

                // Update local user instance
                myUser.setuName(userName);
                myUser.setuEmail(userEmail);

                updateUserFirebase(userName, userEmail);
            }
        }

        private void updateUserFirebase (String userName, String userEmail){
            // Update Firebase Auth User
//        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
//        firebaseUser.updateEmail(userEmail)
//                .addOnSuccessListener(aVoid -> {
//                    // Email updated successfully
//                })
//                .addOnFailureListener(e -> {
//                    // Handle failure
//                });
            // Update Firestore Document
            FirebaseUtil.getUserReference().update("uName", userName, "uEmail", userEmail)
                    .addOnSuccessListener(aVoid -> {
                        MySignal.getInstance().toast("Updated successfully");
                    })
                    .addOnFailureListener(e -> {
                        MySignal.getInstance().toast("Updated failed");
                    });
        }


        private void backToProfile () {
            Fragment profileFragment = new ProfileFragment();

            FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.main_FARM_layout, profileFragment).commit();

        }

        private void findViews (View view){
            settings_IMG_back = view.findViewById(R.id.settings_IMG_back);

            settings_ET_username = view.findViewById(R.id.settings_ET_username);
            settings_ET_email = view.findViewById(R.id.settings_ET_email);
            settings_BTN_saveChanges = view.findViewById(R.id.settings_BTN_saveChanges);

            settings_BTN_showChangePassword = view.findViewById(R.id.settings_BTN_showChangePassword);
            settings_ET_newPassword = view.findViewById(R.id.settings_ET_newPassword);
            settings_BTN_saveNewPassword = view.findViewById(R.id.settings_BTN_saveNewPassword);

            settings_BTN_manageCategories = view.findViewById(R.id.settings_BTN_manageCategories);
            settings_fragment_container = view.findViewById(R.id.settings_fragment_container);
            settings_BTN_saveNewCategories = view.findViewById(R.id.settings_BTN_saveNewCategories);

            settings_BTN_showChangeLocation = view.findViewById(R.id.settings_BTN_showChangeLocation);
            settings_SWITCH_location = view.findViewById(R.id.settings_SWITCH_location);
            settings_ET_address_search = view.findViewById(R.id.settings_ET_address_search);
            settings_BTN_saveNewLocation = view.findViewById(R.id.settings_BTN_saveNewLocation);
        }
    }
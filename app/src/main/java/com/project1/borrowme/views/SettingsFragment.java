package com.project1.borrowme.views;

import static android.content.ContentValues.TAG;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.FrameLayout;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException;
import com.google.firebase.auth.FirebaseUser;
import com.project1.borrowme.R;
import com.project1.borrowme.Utilities.FirebaseUtil;
import com.project1.borrowme.Utilities.LocationManagerUtil;
import com.project1.borrowme.Utilities.MySignal;
import com.project1.borrowme.data.CategoriesData;
import com.project1.borrowme.interfaces.CategorySelectionListener;
import com.project1.borrowme.interfaces.LocationFetchListener;
import com.project1.borrowme.models.Category;
import com.project1.borrowme.models.MyUser;
import com.project1.borrowme.views.CategoriesFragment;
import com.project1.borrowme.screens.ProfileFragment;

import java.util.HashMap;
import java.util.Map;

public class SettingsFragment extends Fragment {

    private MyUser myUser;
    private AppCompatImageButton settings_IMG_back;


    private Map<String, Category> categories;
    private Map<String, Category> selectedCategories;
    private LocationManagerUtil locationManagerUtil;
    private FusedLocationProviderClient fusedLocationClient;

    private TextInputEditText settings_ET_username;
    private TextInputEditText settings_ET_email;
    private MaterialButton settings_BTN_saveChanges;

    private SwitchMaterial settings_SWITCH_changePassword;
    private TextInputEditText settings_ET_newPassword;
    private MaterialButton settings_BTN_saveNewPassword;


    private SwitchMaterial settings_SWITCH_changeLocation;
    private SwitchMaterial settings_SWITCH_getLocationOptions;
    private MaterialCardView settings_CARD_search;
    private MaterialButton settings_BTN_saveNewLocation;


    private SwitchMaterial settings_SWITCH_manageCategories;
    private MaterialButton settings_BTN_saveNewCategories;
    private FrameLayout settings_fragment_container;

    private FrameLayout settings_FRAME_old_password;
    private TextInputEditText settings_ET_oldPassword;
    private MaterialButton settings_BTN_save_old_password;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(view.getContext());
        findViews(view);
        initViews();
        initCategories();
        initCategoriesFragment();
        setupLocationManager();

        return view;
    }

    private void setupLocationManager() {
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment) getChildFragmentManager().findFragmentById(R.id.settings_FRAGMENT_autoComplete);
        locationManagerUtil = new LocationManagerUtil(getContext(), autocompleteFragment, new LocationFetchListener() {
            @Override
            public void onLocationFetched(double lat, double lon) {
                myUser.setLon(lon);
                myUser.setLat(lat);

                FirebaseUtil.currentUserDetails().update("lon", lon, "lat", lat)
                        .addOnSuccessListener(aVoid -> {
                            MySignal.getInstance().toast("Updated successfully");
                        })
                        .addOnFailureListener(e -> {
                            MySignal.getInstance().toast("Updated failed");
                        });

            }

            @Override
            public void onLocationFetchFailed() {
                MySignal.getInstance().toast("Failed to fetch location. Please ensure your location services are enabled, or enter a valid address.");
            }
        });
    }

    private void changeLocation() {
        if (settings_SWITCH_getLocationOptions.isChecked()) {
            // Get current location from GPS
            locationManagerUtil.getCurrentLocation();
        } else {
            // Use the selected location from autocomplete
            locationManagerUtil.triggerLocationUpdate();
        }
    }

    private void toggleLocationSwitchVisibility() {
        settings_SWITCH_getLocationOptions.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // The switch is enabled/checked
                    settings_CARD_search.setVisibility(View.GONE);
                } else {
                    // The switch is disabled/unchecked
                    settings_CARD_search.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void toggleLocationVisibility() {

        if (settings_SWITCH_changeLocation.isChecked()) {
            settings_SWITCH_getLocationOptions.setVisibility(View.VISIBLE);
            settings_BTN_saveNewLocation.setVisibility(View.VISIBLE);
            toggleLocationSwitchVisibility();

        } else {
            settings_SWITCH_getLocationOptions.setVisibility(View.GONE);
            settings_CARD_search.setVisibility(View.GONE);
            settings_BTN_saveNewLocation.setVisibility(View.GONE);
        }

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
                if (category != null) {
                    category.setClicked(true);
                }
            }
        }
    }

    private void updateCategories(Map<String, Category> selectedCategories) {
        this.selectedCategories = selectedCategories;
    }

    private void changeCategories() {

        if (selectedCategories != null && !selectedCategories.isEmpty()) {

            myUser.setCategories(selectedCategories);
            FirebaseUtil.updateUserCategories(myUser.getCategories());
            MySignal.getInstance().toast("Categories updated successfully!");
        } else {
            MySignal.getInstance().toast("You must select at least 1 categories.");
        }
    }

    private void initCategories() {
        categories = new HashMap<>();
        categories = CategoriesData.getCategories();

        synchronizeCategorySelection();
    }

    private void toggleCategoriesListVisibility() {

        if (settings_SWITCH_manageCategories.isChecked()) {
            settings_fragment_container.setVisibility(View.VISIBLE);
            settings_BTN_saveNewCategories.setVisibility(View.VISIBLE);
            settings_ET_username.setVisibility(View.GONE);
                settings_ET_email.setVisibility(View.GONE);
            settings_BTN_saveChanges.setVisibility(View.GONE);
            settings_SWITCH_changePassword.setVisibility(View.GONE);
            settings_ET_newPassword.setVisibility(View.GONE);
            settings_BTN_saveNewPassword.setVisibility(View.GONE);
            settings_SWITCH_changeLocation.setVisibility(View.GONE);
            settings_SWITCH_getLocationOptions.setVisibility(View.GONE);
            settings_CARD_search.setVisibility(View.GONE);
            settings_BTN_saveNewLocation.setVisibility(View.GONE);
        } else {
            settings_fragment_container.setVisibility(View.GONE);
            settings_BTN_saveNewCategories.setVisibility(View.GONE);
            settings_ET_username.setVisibility(View.VISIBLE);
                settings_ET_email.setVisibility(View.VISIBLE);
            settings_BTN_saveChanges.setVisibility(View.VISIBLE);
            settings_SWITCH_changePassword.setVisibility(View.VISIBLE);
            settings_SWITCH_changeLocation.setVisibility(View.VISIBLE);
        }
    }

    private void initCategoriesFragment() {
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


    private void changePassword() {
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
                            if (e instanceof FirebaseAuthRecentLoginRequiredException) {
                                // Prompt the user to re-authenticate
                                MySignal.getInstance().toast("Please re-authenticate to update your password");
                                promptReAuthentication(firebaseUser);
                            } else {
                                MySignal.getInstance().toast("Password update failed: " + e.getMessage());
                            }
                        });
            }
        }

    }

    private void promptReAuthentication(FirebaseUser user) {
        settings_FRAME_old_password.setVisibility(View.VISIBLE);
        settings_BTN_save_old_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String oldPas= settings_ET_oldPassword.getText().toString().trim();

                AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), oldPas);

                user.reauthenticate(credential)
                        .addOnSuccessListener(aVoid -> {
                            // Now call changePassword() again or let the user proceed with other sensitive actions
                            settings_FRAME_old_password.setVisibility(View.GONE);
                            MySignal.getInstance().toast("Re-authentication successful");
                        })
                        .addOnFailureListener(e -> {
                            MySignal.getInstance().toast("Re-authentication failed: " + e.getMessage());
                        });
            }
        });

    }

    private void togglePasswordVisibility() {

        settings_ET_newPassword.setVisibility(settings_SWITCH_changePassword.isChecked() ? View.VISIBLE : View.GONE);
        settings_BTN_saveNewPassword.setVisibility(settings_SWITCH_changePassword.isChecked() ? View.VISIBLE : View.GONE);

    }


    private void saveUserDetails() {
        if (FirebaseUtil.validateUserName(settings_ET_username) && FirebaseUtil.validateEmail(settings_ET_email)) {
            String userName = settings_ET_username.getText().toString().trim();
            String userEmail = settings_ET_email.getText().toString().trim();

            // Update local user instance
            myUser.setuName(userName);
            myUser.setuEmail(userEmail);

            updateUserFirebase(userName, userEmail);
        }
    }

    private void updateUserFirebase(String userName, String userEmail) {
        // Update Firebase Auth User
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if (firebaseUser != null) {
            Task<Void> voidTask = firebaseUser.updateEmail(userEmail)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "User email address updated.");
                            }else{
                                promptReAuthentication(firebaseUser);
                            }
                        }
                    });
        }
        // Update Firestore Document
        FirebaseUtil.currentUserDetails().update("uName", userName, "uEmail", userEmail)
                .addOnSuccessListener(aVoid -> {
                    MySignal.getInstance().toast("Updated successfully");
                })
                .addOnFailureListener(e -> {
                    MySignal.getInstance().toast("Updated failed");
                });
    }


    private void backToProfile() {
        Fragment profileFragment = new ProfileFragment();

        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_FARM_layout, profileFragment).commit();

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

        settings_SWITCH_changePassword.setOnClickListener(v -> togglePasswordVisibility());
        settings_BTN_saveNewPassword.setOnClickListener(v -> changePassword());


        settings_BTN_saveNewLocation.setOnClickListener(v -> changeLocation());
        settings_SWITCH_changeLocation.setOnClickListener(v -> toggleLocationVisibility());

        settings_SWITCH_manageCategories.setOnClickListener(v -> toggleCategoriesListVisibility());
        settings_BTN_saveNewCategories.setOnClickListener(v -> changeCategories());
    }

    private void findViews(View view) {
        settings_IMG_back = view.findViewById(R.id.settings_IMG_back);

        settings_ET_username = view.findViewById(R.id.settings_ET_username);
        settings_ET_email = view.findViewById(R.id.settings_ET_email);
        settings_BTN_saveChanges = view.findViewById(R.id.settings_BTN_saveChanges);

        settings_SWITCH_changePassword = view.findViewById(R.id.settings_SWITCH_changePassword);
        settings_ET_newPassword = view.findViewById(R.id.settings_ET_newPassword);
        settings_BTN_saveNewPassword = view.findViewById(R.id.settings_BTN_saveNewPassword);

        settings_SWITCH_manageCategories = view.findViewById(R.id.settings_SWITCH_manageCategories);
        settings_fragment_container = view.findViewById(R.id.settings_fragment_container);
        settings_BTN_saveNewCategories = view.findViewById(R.id.settings_BTN_saveNewCategories);

        settings_SWITCH_changeLocation = view.findViewById(R.id.settings_SWITCH_changeLocation);
        settings_SWITCH_getLocationOptions = view.findViewById(R.id.settings_SWITCH_getLocationOptions);
        settings_CARD_search = view.findViewById(R.id.settings_CARD_search);
        settings_BTN_saveNewLocation = view.findViewById(R.id.settings_BTN_saveNewLocation);

        settings_FRAME_old_password = view.findViewById(R.id.settings_FRAME_old_password);
        settings_ET_oldPassword = view.findViewById(R.id.settings_ET_oldPassword);
        settings_BTN_save_old_password=view.findViewById(R.id.settings_BTN_save_old_password);
    }
}
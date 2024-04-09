package com.project1.borrowme;

import android.os.Bundle;

import androidx.appcompat.widget.AppCompatImageButton;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.google.android.material.button.MaterialButton;
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
    private FrameLayout settings_fragment_container;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        findViews(view);
        initCategories();
        initFragment();
        initViews();
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
        myUser.setCategories(selectedCategories);
    }

    private void initFragment() {
        CategoriesFragment categoriesFragment = new CategoriesFragment();
        CategorySelectionListener listener = new CategorySelectionListener() {
            @Override
            public void onCategorySelectionUpdated(Map<String, Category> selectedCategories) {
                if (myUser.getCategories().size() > 1) {
                    updateCategories(selectedCategories);
                } else {
                    MySignal.getInstance().toast("MUST HAVE AT LEAST 1 CATEGORY");
                }
            }
        };

        categoriesFragment.setSelectionListener(listener);


        if (categories != null) {
            categoriesFragment.initCategories(categories);
        }
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.settings_fragment_container, categoriesFragment, "CATEGORIES_FRAGMENT_TAG").commit();

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

        settings_BTN_showChangePassword.setOnClickListener(v -> togglePasswordVisibility(!passwordIsClicked));
        settings_BTN_saveNewPassword.setOnClickListener(v -> changePassword());

        settings_BTN_manageCategories.setOnClickListener(v -> toggleCategoriesListVisibility(categoriesListIsClicked));
    }

    private void togglePasswordVisibility(boolean show) {
        settings_ET_newPassword.setVisibility(show ? View.VISIBLE : View.GONE);
        settings_BTN_saveNewPassword.setVisibility(show ? View.VISIBLE : View.GONE);
        passwordIsClicked = !passwordIsClicked;
    }

    private void toggleCategoriesListVisibility(boolean show) {
        settings_fragment_container.setVisibility(show ? View.VISIBLE : View.GONE);
        categoriesListIsClicked = !categoriesListIsClicked;
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
                            MySignal.getInstance().toast("Password updated failed");
                        });
            }
        }
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


    private void backToProfile() {
        Fragment profileFragment = new ProfileFragment();

        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_FARM_layout, profileFragment).commit();

    }


    private void findViews(View view) {
        settings_IMG_back = view.findViewById(R.id.settings_IMG_back);
        settings_ET_username = view.findViewById(R.id.settings_ET_username);
        settings_ET_email = view.findViewById(R.id.settings_ET_email);
        settings_BTN_saveChanges = view.findViewById(R.id.settings_BTN_saveChanges);
        settings_BTN_showChangePassword = view.findViewById(R.id.settings_BTN_showChangePassword);
        settings_ET_newPassword = view.findViewById(R.id.settings_ET_newPassword);
        settings_BTN_saveNewPassword = view.findViewById(R.id.settings_BTN_saveNewPassword);
        settings_BTN_manageCategories = view.findViewById(R.id.settings_BTN_manageCategories);
        settings_fragment_container = view.findViewById(R.id.settings_fragment_container);
    }
}
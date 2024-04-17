package com.project1.borrowme.screens;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;
import com.project1.borrowme.R;
import com.project1.borrowme.models.TheUser;
import com.project1.borrowme.views.SettingsFragment;
import com.project1.borrowme.Utilities.FirebaseUtil;
import com.project1.borrowme.Utilities.MySignal;
import com.project1.borrowme.adpters.CategoryAdapter;
import com.project1.borrowme.interfaces.CallbackCategory;
import com.project1.borrowme.models.Category;
import com.project1.borrowme.models.UserDetails;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ProfileFragment extends Fragment {
    TheUser theUser;
    private UserDetails userDetails;
    ActivityResultLauncher<Intent> resultLauncher;
    private Map<String, Category> categories = new HashMap<>();
    private MaterialTextView profile_MTV_categoryNum;
    private AppCompatImageButton profile_IMB_settings;
    private ShapeableImageView profile_IMG_profile_picture;
    private FloatingActionButton profile_FAB_change_image;
    private MaterialTextView profile_TV_user_location;
    private RecyclerView profile_RECYCLER_categories;
    private RatingBar profile_RB_rating;
    private MaterialTextView profile_MTV_name;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        setUser();
        findViews(view);
        registerResult();
        initViews(getContext());

        return view;
    }

    private void setUser() {
        theUser =TheUser.getInstance();
        userDetails=theUser.getUserDetails();
        int x=1;
    }


    private void setAddress()  {
        Geocoder geocoder = new Geocoder(requireContext(), Locale.getDefault());
        double latitude = userDetails.getLat();
        double longitude = userDetails.getLon();
        List<Address> addresses = null;
        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (!addresses.isEmpty()) {
            Address address = addresses.get(0);
            String location = address.getAddressLine(0);
            profile_TV_user_location.setText(location);
        } else {
            try {
                throw new IOException("No address found for coordinates: " + latitude + ", " + longitude);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void pickImage() {
        Intent intent = new Intent(MediaStore.ACTION_PICK_IMAGES);
        resultLauncher.launch(intent);
    }

    private void registerResult() {
        resultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                try {
                    Uri imageUri = result.getData().getData();
                    if (imageUri != null) {
                        FirebaseUtil.getCurrentProfilePicStorageRef().putFile(imageUri);
                        userDetails.setProfileImageUri(imageUri);
                        setProfilePic(imageUri);
                    }

                } catch (Exception e) {
                    MySignal.getInstance().toast("No Image Selected");
                }
            }
        });
    }

    private void setProfilePic(Uri imageUri) {
        if (imageUri != null) {
            Glide.with(this).load(imageUri).into(profile_IMG_profile_picture);
        } else {
            profile_IMG_profile_picture.setImageResource(R.drawable.farmerprofile);
        }
    }

    private void changeToSettingsFragment() {
        Fragment settingsFragment = new SettingsFragment();

        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_FARM_layout, settingsFragment).commit();
    }


    private void initCategories() {
        categories = userDetails.getCategories();
    }

    private void setCategoriesAdapter(Context context) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        linearLayoutManager.setOrientation(RecyclerView.HORIZONTAL);
        profile_RECYCLER_categories.setLayoutManager(linearLayoutManager);

        CategoryAdapter adapter = new CategoryAdapter(categories, getContext(), new CallbackCategory() {
            @Override
            public void addCategory(Category category) {
                // Implement adding category logic
                userDetails.addCategory(category);
            }

            @Override
            public void removeCategory(Category category) {
                // Implement removing category logic
                userDetails.removeCategory(category.getName());
            }
        });
        profile_RECYCLER_categories.setAdapter(adapter);
    }



    private void initViews(Context context)  {

        if (userDetails != null) {
            profile_MTV_name.setText(userDetails.getuName());
            setAddress();
            setProfilePic(userDetails.getProfileImageUri());

            profile_MTV_categoryNum.setText(String.format("%d", userDetails.getCategories().size()));
            initCategories();
            setCategoriesAdapter(context);
        }

        profile_IMB_settings.setOnClickListener(v -> changeToSettingsFragment());

        profile_FAB_change_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickImage();
            }
        });
    }

    private void findViews(View view) {
        profile_IMB_settings = view.findViewById(R.id.profile_IMB_settings);
        profile_IMG_profile_picture = view.findViewById(R.id.profile_IMG_profile_picture);
        profile_TV_user_location = view.findViewById(R.id.profile_TV_user_location);
        profile_RECYCLER_categories = view.findViewById(R.id.profile_RECYCLER_categories);
        profile_RB_rating = view.findViewById(R.id.profile_RB_rating);
        profile_MTV_name = view.findViewById(R.id.profile_MTV_name);
        profile_MTV_categoryNum = view.findViewById(R.id.profile_MTV_categoryNum);
        profile_FAB_change_image = view.findViewById(R.id.profile_FAB_change_image);
    }
}
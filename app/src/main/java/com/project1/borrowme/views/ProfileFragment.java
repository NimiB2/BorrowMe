package com.project1.borrowme.views;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.widget.AppCompatImageButton;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;

import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;
import com.project1.borrowme.MainActivity;
import com.project1.borrowme.R;
import com.project1.borrowme.SettingsFragment;
import com.project1.borrowme.adpters.CategoryAdapter;
import com.project1.borrowme.interfaces.CallbackCategory;
import com.project1.borrowme.models.Category;
import com.project1.borrowme.models.MyUser;

import java.util.HashMap;
import java.util.Map;

public class ProfileFragment extends Fragment {
    MyUser myUser = MyUser.getInstance();
    private Map<String, Category> categories = new HashMap<>();
    private MaterialTextView profile_MTV_categoryNum;
    private AppCompatImageButton profile_IMB_settings;
    private ShapeableImageView profile_IMG_profile_picture;
    private MaterialTextView profile_TV_user_location;
    private RecyclerView profile_RECYCLER_categories;
    private RatingBar profile_RB_rating;
    private MaterialTextView profile_MTV_name;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        findViews(view);
        initViews(getContext());

        return view;
    }

    private void initViews(Context context) {

        if (myUser != null) {
            profile_MTV_name.setText(myUser.getuName());
            profile_MTV_categoryNum.setText(String.format("%d", myUser.getCategories().size()));
            initCategories();
            setAdapter(context);
        }

        profile_IMB_settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeToSettingsFragment();
            }
        });
    }

    private void changeToSettingsFragment() {
        Fragment settingsFragment = new SettingsFragment();

        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_FARM_layout, settingsFragment).commit();
    }


    private void initCategories() {
        categories = myUser.getCategories();
    }

    private void setAdapter(Context context) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        linearLayoutManager.setOrientation(RecyclerView.HORIZONTAL);
        profile_RECYCLER_categories.setLayoutManager(linearLayoutManager);

        CategoryAdapter adapter = new CategoryAdapter(categories, getContext(), new CallbackCategory() {
            @Override
            public void addCategory(Category category) {
                // Implement adding category logic
                myUser.addCategory(category);
            }

            @Override
            public void removeCategory(Category category) {
                // Implement removing category logic
                myUser.removeCategory(category.getName());
            }
        });
        profile_RECYCLER_categories.setAdapter(adapter);
    }

    private void findViews(View view) {
        profile_IMB_settings = view.findViewById(R.id.profile_IMB_settings);
        profile_IMG_profile_picture = view.findViewById(R.id.profile_IMG_profile_picture);
        profile_TV_user_location = view.findViewById(R.id.profile_TV_user_location);
        profile_RECYCLER_categories = view.findViewById(R.id.profile_RECYCLER_categories);
        profile_RB_rating = view.findViewById(R.id.profile_RB_rating);
        profile_MTV_name = view.findViewById(R.id.profile_MTV_name);
        profile_MTV_categoryNum= view.findViewById(R.id.profile_MTV_categoryNum);
    }
}
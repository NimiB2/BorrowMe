package com.project1.borrowme.views;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;

import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;
import com.project1.borrowme.R;
import com.project1.borrowme.models.MyUser;

public class ProfileFragment extends Fragment {
    private ShapeableImageView profile_IMG_settings;
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
        initViews(view);

        return view;
    }

    private void initViews(View view) {
        String name=MyUser.getInstance().getuName();
        if(name!=null){
            profile_MTV_name.setText(name);
        }
    }

    private void findViews(View view) {
        profile_IMG_settings = view.findViewById(R.id.profile_IMG_settings);
        profile_IMG_profile_picture = view.findViewById(R.id.profile_IMG_profile_picture);
        profile_TV_user_location = view.findViewById(R.id.profile_TV_user_location);
        profile_RECYCLER_categories = view.findViewById(R.id.profile_RECYCLER_categories);
        profile_RB_rating = view.findViewById(R.id.profile_RB_rating);
        profile_MTV_name= view.findViewById(R.id.profile_MTV_name);
    }
}
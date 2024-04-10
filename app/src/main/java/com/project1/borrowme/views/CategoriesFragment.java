package com.project1.borrowme.views;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.textview.MaterialTextView;
import com.project1.borrowme.R;
import com.project1.borrowme.adpters.CategoryAdapter;
import com.project1.borrowme.data.CategoriesData;
import com.project1.borrowme.interfaces.CallbackCategory;
import com.project1.borrowme.interfaces.CategorySelectionListener;
import com.project1.borrowme.models.Category;
import com.project1.borrowme.models.MyUser;

import java.util.HashMap;
import java.util.Map;

public class CategoriesFragment extends Fragment {
    //private final MyUser myUser = MyUser.getInstance();
    private final int NUM_OF_COLS = 3;
    private RecyclerView recyclerViewCategories;
    private MaterialTextView MTV_categoryNum;
    private CallbackCategory callbackCategory;
    private CategorySelectionListener selectionListener;
    private Map<String, Category> selectedCategories = new HashMap<>();
    private Map<String, Category> categories;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_categories, container, false);

        findViews(view);
        updateNumCategoryText(MyUser.getInstance().getCategories().size());
        setAdapter(getContext());


        return view;
    }


    private void notifySelectionUpdated() {
        if (selectionListener != null) {
            selectionListener.onCategorySelectionUpdated(selectedCategories);
        }
    }
    public void setSelectionListener(CategorySelectionListener listener) {
        this.selectionListener = listener;
    }

    private void setAdapter(Context context) {
        CategoryAdapter adapter = new CategoryAdapter(categories, context, callbackCategory);

        adapter.setCallback(new CallbackCategory() {
            @Override
            public void addCategory(Category category) {
                selectedCategories.put(category.getName(), category);
                notifySelectionUpdated();
                updateNumCategoryText(selectedCategories.size());
            }

            @Override
            public void removeCategory(Category category) {
                selectedCategories.remove(category.getName());
                notifySelectionUpdated();
                updateNumCategoryText(selectedCategories.size());
            }
        });


        recyclerViewCategories.setLayoutManager(new GridLayoutManager(getActivity(), NUM_OF_COLS));
        recyclerViewCategories.setAdapter(adapter);
    }

    private void updateNumCategoryText(int size) {
        MTV_categoryNum.setText(String.format("%d", size));
    }

    public void initCategories(Map<String, Category> categories) {
        this.categories = categories;
    }

    private void findViews(View view) {
        recyclerViewCategories = view.findViewById(R.id.fragment_RECYCLER_categoryList);
        MTV_categoryNum = view.findViewById(R.id.MTV_categoryNum);
    }

}
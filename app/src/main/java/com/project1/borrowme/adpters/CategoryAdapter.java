package com.project1.borrowme.adpters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.project1.borrowme.R;
import com.project1.borrowme.Utilities.MySignal;
import com.project1.borrowme.interfaces.Callback_Category;
import com.project1.borrowme.models.Category;

import java.util.ArrayList;
import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {
    private static final long VIBRATION = 500;
    private List<Category> categories;
    private Context context;
    private Callback_Category callbackCategory;


    public CategoryAdapter(List<Category> categories, Context context, Callback_Category callbackCategory) {
        this.categories = categories;
        this.context = context;
        this.callbackCategory = callbackCategory;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_item, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        String categoryName = categories.get(position).getName();
        int categoryImage = categories.get(position).getImage();

        holder.categoryName.setText(categoryName);
        holder.imageCategory.setImageResource(categoryImage);
    }

    public void setCallback(Callback_Category callbackCategory) {
        this.callbackCategory = callbackCategory;
    }

    @Override
    public int getItemCount() {
        return categories == null ? 0 : categories.size();
    }

    private Category getCategory(int position) {
        return categories.get(position);
    }

    public class CategoryViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageCategory;
        private TextView categoryName;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            imageCategory = itemView.findViewById(R.id.imageCategory);
            categoryName = itemView.findViewById(R.id.categoryName);

            imageCategory.setOnClickListener(v ->
            {
                if (callbackCategory != null) {
                    Category category = getCategory(getAdapterPosition());
                    category.setClicked(!category.isClicked());
                    updateUI(category,category.isClicked());
                }
            });
        }

        private void updateUI(Category category,boolean clicked) {
            if(clicked){
                MySignal.getInstance().vibrate(VIBRATION);
                MySignal.getInstance().toast(category.getName()+" has been selected successfully");
                imageCategory.setImageAlpha(128);
                categoryName.setAlpha(0.5f);
                callbackCategory.addCategory(category);

            } else {
                imageCategory.setImageAlpha(255);
                categoryName.setAlpha(1.0f);
                callbackCategory.removeCategory(category);
            }

        }
    }
}


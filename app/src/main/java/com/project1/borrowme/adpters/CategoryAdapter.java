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
import com.project1.borrowme.interfaces.CallbackCategory;
import com.project1.borrowme.models.Category;

import java.lang.ref.WeakReference;
import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {
    private static final long VIBRATION = 500;
    private List<Category> categories;
    private WeakReference<CallbackCategory> callbackCategoryRef;

    private Context context;
    private CallbackCategory callbackCategory;


    public CategoryAdapter(List<Category> categories, Context context, CallbackCategory callbackCategory) {
        this.categories = categories;
        this.context = context;
        this.callbackCategoryRef = new WeakReference<>(callbackCategory);
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

    public void setCallback(CallbackCategory callbackCategory) {
        this.callbackCategory = callbackCategory;
    }

    @Override
    public int getItemCount() {
        return categories == null ? 0 : categories.size();
    }
    private Category getCategory(int position) {
        if (categories != null && position >= 0 && position < categories.size()) {
            return categories.get(position);
        }
        return null;
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
                CallbackCategory callbackCategory = callbackCategoryRef.get();
                if (callbackCategory != null) {
                    Category category = getCategory(getAdapterPosition());
                    if (category != null) {
                        category.setClicked(!category.isClicked());
                        updateUI(category, category.isClicked());
                    }
                }
            });
        }

        private void updateUI(Category category, boolean clicked) {
            itemView.post(() -> {
                if (clicked) {
                    MySignal.getInstance().vibrate(VIBRATION);
                    imageCategory.setImageAlpha(128);
                    categoryName.setAlpha(0.5f);
                    CallbackCategory callbackCategory = callbackCategoryRef.get();
                    if (callbackCategory != null) {
                        callbackCategory.addCategory(category);
                    }
                } else {
                    imageCategory.setImageAlpha(255);
                    categoryName.setAlpha(1.0f);
                    CallbackCategory callbackCategory = callbackCategoryRef.get();
                    if (callbackCategory != null) {
                        callbackCategory.removeCategory(category);
                    }
                }
            });
        }
    }
}


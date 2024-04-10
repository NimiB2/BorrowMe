package com.project1.borrowme.adpters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.project1.borrowme.R;
import com.project1.borrowme.Utilities.MySignal;
import com.project1.borrowme.interfaces.CallbackCategory;
import com.project1.borrowme.models.Category;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {
    private static final long VIBRATION = 500;
    private List<Category> categories = new ArrayList<>();
    private Context context;
    private CallbackCategory callbackCategory;


    public CategoryAdapter(Map<String, Category> categories, Context context, CallbackCategory callbackCategory) {
        this.categories = new ArrayList<>(categories.values());
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
        Category category = categories.get(position);

        if (category != null) {
            String categoryName = category.getName();
            int categoryImage = category.getImage();

            holder.categoryName.setText(categoryName);
            holder.imageCategory.setImageResource(categoryImage);
            holder.updateUI(category, category.isClicked());

        }
    }

    public void setCallback(CallbackCategory callbackCategory) {
        this.callbackCategory = callbackCategory;
    }

    @Override
    public int getItemCount() {
        return categories == null ? 0 : categories.size();
    }

    private Category getCategory(int position) {
        if (position >= 0 && position < categories.size()) {
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
                    imageCategory.setImageAlpha(255);
                    categoryName.setAlpha(1.0f);
                    itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.background));

                    callbackCategory.addCategory(category);


                } else {
                    imageCategory.setImageAlpha(128);
                    categoryName.setAlpha(0.5f);
                    itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.backgroundUnclicked));

                    callbackCategory.removeCategory(category);

                }
            });
        }
    }
}


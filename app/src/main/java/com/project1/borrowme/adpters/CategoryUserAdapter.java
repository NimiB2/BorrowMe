package com.project1.borrowme.adpters;

import com.project1.borrowme.models.Category;

public class CategoryUserAdapter {
    private Category category;

    public CategoryUserAdapter() {
    }

    public String getCategory() {
        return category.getName();
    }

    public CategoryUserAdapter setCategory(Category category) {
        this.category = category;
        return this;
    }
}

package com.project1.borrowme.interfaces;

import com.project1.borrowme.models.Category;

import java.util.Map;

public interface CategorySelectionListener {
    void onCategorySelectionUpdated(Map<String, Category> selectedCategories);}

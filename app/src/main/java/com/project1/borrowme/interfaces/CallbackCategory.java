package com.project1.borrowme.interfaces;

import com.project1.borrowme.models.Category;

public interface CallbackCategory {
    void addCategory(Category category);

    void removeCategory(Category category);
}

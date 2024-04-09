package com.project1.borrowme.data;

import com.project1.borrowme.R;
import com.project1.borrowme.models.Category;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CategoriesData {

    private static final String[] names = new String[]{
            "Books",
            "Tools",
            "Clothing",
            "Sport",
            "Camping",
            "Sea Equipment",
            "Electronics and Gadgets",
            "Kitchen Products",
            "Toys and Games",
            "Automotive World",
            "Equipment for Events",
            "Art and Craft Supplies",
            "Gardening Equipment",
    };

    private static final int[] images = new int[]{
            R.drawable.books,
            R.drawable.tools,
            R.drawable.clothing,
            R.drawable.sports,
            R.drawable.camping,
            R.drawable.sea,
            R.drawable.gadget,
            R.drawable.kitchen,
            R.drawable.toys,
            R.drawable.automotive,
            R.drawable.event,
            R.drawable.art,
            R.drawable.gardening
    };

    public static  Map<String, Category> getCategories() {
        Map<String, Category> categories = new HashMap<>();
        for (int i = 0; i < names.length; i++) {
            Category category = new Category()
                    .setName(names[i])
                    .setImage(images[i]);
            categories.put(names[i], category);
        }
        return categories;
    }

}

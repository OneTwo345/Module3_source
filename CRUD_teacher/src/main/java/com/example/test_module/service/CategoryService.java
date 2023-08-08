package com.example.test_module.service;

import com.example.test_module.dao.CategoryDAO;
import com.example.test_module.model.Category;

import java.util.List;

public class CategoryService {
    public static List<Category> getCategories(){
        return new CategoryDAO()
                .getCategory();
    }
}

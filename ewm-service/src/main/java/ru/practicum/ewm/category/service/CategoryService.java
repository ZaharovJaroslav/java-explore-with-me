package ru.practicum.ewm.category.service;

import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.dto.NewCategoryRequest;
import java.util.List;

public interface CategoryService {
    CategoryDto addCategory(NewCategoryRequest name);

    CategoryDto updateCategory(Long categoryId, NewCategoryRequest newCategory);

    void deleteCategoryById(Long id);

    List<CategoryDto> getCategories(int from, int size);

    CategoryDto getCategoryById(Long id);
}
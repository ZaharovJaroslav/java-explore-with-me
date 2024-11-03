package ru.practicum.ewm.category.service;


import org.springframework.data.domain.Pageable;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.dto.NewCategoryDto;

import java.util.List;


public interface CategoryService {

    public CategoryDto addCategoryAdmin(NewCategoryDto newCategoryDto);

    public void deleteCategoryAdmin(Long categoryId);

    public CategoryDto updateCategoryAdmin(CategoryDto categoryDto);

    public List<CategoryDto> getCategories(Pageable pageable);

    public CategoryDto getCategory(Long categoryId);
}

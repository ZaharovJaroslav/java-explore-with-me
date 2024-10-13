package ru.practicum.ewm.category.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.dto.NewCategoryRequest;
import ru.practicum.ewm.category.mapper.CategoryMapper;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.category.repository.CategoryRepository;
import ru.practicum.ewm.category.validator.CreateCategoryValidator;
import ru.practicum.ewm.category.validator.GetCategoryValidator;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.exception.ValidationException;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;

    @Override
    public CategoryDto addCategory(NewCategoryRequest request) {
        log.debug("addCategory({})", request);
        CreateCategoryValidator validator = new CreateCategoryValidator(request);
        validator.validate();
        if (!validator.isValid()) {
            throw new ValidationException("Запрос составлен некорректно", validator.getMessages());
        }
        Category category = new Category(request.getName());
        try {
        return CategoryMapper.toCategoryDto(categoryRepository.save(category));
        } catch (Exception e) {
            throw new ConflictException(e.getMessage(), new ConflictException("Нарушение целостности данных"));
        }
    }

    @Override
    public CategoryDto updateCategory(Long categoryId, NewCategoryRequest newCategory) {
        log.debug("updateCategory({})", categoryId);
        CreateCategoryValidator validator = new CreateCategoryValidator(newCategory);
        validator.validate();
        if (!validator.isValid()) {
            throw new ValidationException("Запрос составлен некорректно", validator.getMessages());
        }
        if (categoryRepository.findById(categoryId).isEmpty()) {
            throw new NotFoundException("Нужный обьект не найден:",
                    new NotFoundException("Категория с id = " + categoryId + "не найдена"));
        }
        Category udateCategory = new Category(categoryId,newCategory.getName());
        try {
            return CategoryMapper.toCategoryDto(categoryRepository.save(udateCategory));
        } catch (Exception e) {
        throw new ConflictException(e.getMessage(), new ConflictException("Нарушение целостности данных"));
    }
    }

    @Override
    public void deleteCategoryById(Long categoryId) {
        log.debug("deleteCategoryById({})", categoryId);
        if (categoryRepository.findById(categoryId).isPresent()) {
            categoryRepository.deleteById(categoryId);
        } else
            throw new NotFoundException("Нужный обьект не найден:",
                    new NotFoundException("Категория с id = " + categoryId + "не найдена"));
    }

    @Override
    public List<CategoryDto> getCategories(int from, int size) {
        log.debug("getCategories({}, {})", from, size);
        GetCategoryValidator validator = new GetCategoryValidator(List.of(from,size));
        validator.validate();
        Pageable pageable =  PageRequest.of(from, size, Sort.by("id").ascending());
        List<Category> categories;
        categories = categoryRepository.findAll(pageable).getContent();
        return categories.stream().map(CategoryMapper::toCategoryDto).toList();
    }

    @Override
    public CategoryDto getCategoryById(Long categoryId) {
        log.debug("getCategoryById({})", categoryId);
        Optional<Category> category =  categoryRepository.findById(categoryId);
        if (category.isPresent()) {
            return CategoryMapper.toCategoryDto(category.get());
        } else {
            throw new NotFoundException("Нужный обьект не найден:",
                    new NotFoundException("Категория с id = " + categoryId + "не найдена"));
        }
    }
}
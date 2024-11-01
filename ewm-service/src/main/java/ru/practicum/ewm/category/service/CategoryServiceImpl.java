package ru.practicum.ewm.category.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.dto.NewCategoryRequest;
import ru.practicum.ewm.category.mapper.CategoryMapper;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.category.repository.CategoryRepository;
import ru.practicum.ewm.category.validator.CreateCategoryValidator;
import ru.practicum.ewm.category.validator.GetCategoryValidator;
import ru.practicum.ewm.event.repository.event.EventRepository;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.exception.ValidationException;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;

    @Override
    public CategoryDto addCategory(NewCategoryRequest newCategoryRequest) {
        log.debug("addCategory({})", newCategoryRequest);
        CreateCategoryValidator validator = new CreateCategoryValidator(newCategoryRequest);
        validator.validate();
        if (!validator.isValid()) {
            throw new ValidationException("Запрос составлен некорректно", validator.getMessages());
        }
        getCategoryByName(newCategoryRequest.getName());
        Category category = new Category(newCategoryRequest.getName());
        return CategoryMapper.toCategoryDto(categoryRepository.save(category));
    }

    public void getCategoryByName(String name) {
        log.info("Запрос на получение категории по названию");
        if (categoryRepository.findCategoryByName(name).isPresent()) {
            throw new ConflictException("Категория " + name + " уже существует");
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
        Category category =  categoryRepository.findById(categoryId).orElseThrow(() ->
                new NotFoundException("\"Категория с id = \" + categoryId + \"не найдена\""));
        if (category.getName().equals(newCategory.getName())) {
            categoryRepository.save(category);
        } else {
            getCategoryByName(newCategory.getName());
            category.setName(newCategory.getName());
            categoryRepository.save(category);
        }
        return CategoryMapper.toCategoryDto(categoryRepository.save(category));
    }

    @Override
    @Transactional
    public void deleteCategoryById(Long categoryId) {
        log.debug("deleteCategoryById({})", categoryId);
        categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException("Подборка с id = " + categoryId + "не найден"));
        if (!eventRepository.findEventsByCategoryId(categoryId).isEmpty()) {
            throw new ConflictException("Нельзя удалить категорию так как в нем содержаться события");
        }
        categoryRepository.deleteById(categoryId);;
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
        Category category =  categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException("Подборка с id = " + categoryId + "не найден"));
        return CategoryMapper.toCategoryDto(category);
    }
}
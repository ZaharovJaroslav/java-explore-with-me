package ru.practicum.ewm.category.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.service.CategoryService;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/categories")
public class CategoryPublicController {
    private final CategoryService categoryService;

    @GetMapping
    public List<CategoryDto> getCategories(@RequestParam(value = "from", required = false, defaultValue = "0") int from,
                                           @RequestParam(value = "size", required = false, defaultValue = "10") int size) {
        log.info("Запрос на получение всех категорий по заданным парметрам: {}, {}", from, size);
        return categoryService.getCategories(from, size);
    }

    @GetMapping("/{categoryId}")
    public CategoryDto getCategoryById(@PathVariable long categoryId) {
        log.info("Запрос на получение категории по id: {}", categoryId);
        return categoryService.getCategoryById(categoryId);
    }
}

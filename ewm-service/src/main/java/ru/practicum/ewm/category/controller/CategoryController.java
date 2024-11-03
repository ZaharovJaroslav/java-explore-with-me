package ru.practicum.ewm.category.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.dto.NewCategoryDto;
import ru.practicum.ewm.category.service.CategoryService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping("/admin/categories")
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto addCategoryAdmin(@Valid @RequestBody NewCategoryDto newCategoryDto) {
        return categoryService.addCategoryAdmin(newCategoryDto);
    }

    @DeleteMapping("/admin/categories/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategoryAdmin(@PathVariable Long id) {
        categoryService.deleteCategoryAdmin(id);
    }

    @PatchMapping("/admin/categories/{id}")
    @ResponseStatus(HttpStatus.OK)
    public CategoryDto updateCategoryAdmin(@Valid @PathVariable Long id, @Valid @RequestBody CategoryDto categoryDto) {
        categoryDto.setId(id);
        return categoryService.updateCategoryAdmin(categoryDto);
    }


    @GetMapping("/categories")
    @ResponseStatus(HttpStatus.OK)
    public List<CategoryDto> getCategories(@RequestParam(required = false, defaultValue = "0") @Min(0) Integer from,
                                           @RequestParam(required = false, defaultValue = "10") @Min(1) Integer size) {
        return categoryService.getCategories(PageRequest.of(from / size, size));
    }

    @GetMapping("/categories/{id}")
    @ResponseStatus(HttpStatus.OK)
    public CategoryDto getCategory(@PathVariable Long id) {
        return categoryService.getCategory(id);
    }


}

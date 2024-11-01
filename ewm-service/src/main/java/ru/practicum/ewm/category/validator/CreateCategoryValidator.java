package ru.practicum.ewm.category.validator;

import lombok.extern.slf4j.Slf4j;
import ru.practicum.ewm.category.dto.NewCategoryRequest;
import ru.practicum.ewm.validator.AbstractValidator;

@Slf4j
public class CreateCategoryValidator extends AbstractValidator {
    protected final NewCategoryRequest request;

    public CreateCategoryValidator(NewCategoryRequest request) {
        this.request = request;
    }

    public void validate() {
        log.debug("validate({})",request);
        if (request.getName() == null || request.getName().isBlank()) {
            log.error("Не задано название категории, Переданное значение: {}", request);
            validateResult.add("Не задано название категории, Переданное значение: " + request.getName());
        }
        if (request.getName() != null && (request.getName().length() > 50 || request.getName().length() < 2)) {
            log.error("Имя пользователя может содержать не более 50 и не менее 1 символа," +
                    " Переданное значение: {}", request.getName().length());
            validateResult.add("Имя пользователя может содержать не более 50 и не менее 1 символа," +
                    " Переданное значение: " + request.getName().length());
        }
    }
}


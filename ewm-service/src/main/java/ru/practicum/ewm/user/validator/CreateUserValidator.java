package ru.practicum.ewm.user.validator;

import lombok.extern.slf4j.Slf4j;
import ru.practicum.ewm.user.dto.NewUserRequest;
import ru.practicum.ewm.validator.AbstractValidator;

@Slf4j
public class CreateUserValidator extends AbstractValidator {
    protected final NewUserRequest request;

    public CreateUserValidator(NewUserRequest request) {
        this.request = request;
    }

    public void validate() {
        log.debug("validate({})",request);
        if (request.getName() == null || request.getName().isBlank()) {
            log.error("Не задано имя пользоватля, Переданное значение: {}", request);
            validateResult.add("Не задано имя пользоватля, Переданное значение: " + request.getName());
        }
        if (request.getEmail() == null || request.getEmail().isBlank()) {
            log.error("Не задано имя пользоватля, Переданное значение: {}", request);
            validateResult.add("Не задано имя пользоватля, Переданное значение: " + request.getEmail());
        }
        if (request.getName().length() > 250 || request.getName().length() < 2) {
            log.error("Имя пользователя может содержать не более 250 и не менее 2 символов," +
                    " Переданное значение: {}", request.getName().length());
            validateResult.add("Имя пользователя может содержать не более 250 и не менее 2 символов," +
                    " Переданное значение: " + request.getName().length());
        }
        if (request.getEmail().length() > 254 || request.getEmail().length() < 6) {
            log.error("email пользователя может содержать не более 250 и не менее 2 символов," +
                    " Переданное значение: {}", request.getEmail().length());
            validateResult.add("email пользователя может содержать не более 250 и не менее 2 символов," +
                    " Переданное значение: " + request.getEmail().length());
        }
        if (!request.getEmail().contains("@")) {
            log.error("email пользователя длжен содеражть симивол - @," +
                    " Переданное значение: {}", request.getEmail());
            validateResult.add("email пользователя длжен содеражть симивол - @," +
                    " Переданное значение: " + request.getEmail());
        }
    }
}

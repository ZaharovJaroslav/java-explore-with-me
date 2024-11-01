package ru.practicum.ewm.user.validator;

import lombok.extern.slf4j.Slf4j;
import ru.practicum.ewm.exception.ValidationException;
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
        if (request.getEmail() == null) {
            log.error("Не задана Электронная почта пользоватля, Переданное значение: {}", request);
            validateResult.add("Не задано имя пользоватля, Переданное значение: " + request.getEmail());
        }
        if (request.getEmail() != null && request.getEmail().isBlank()) {
            log.error("Электронная почта пользоватля содержит пустое значение, Переданное значение: {}", request);
            validateResult.add("Не задано имя пользоватля, Переданное значение: " + request.getEmail());
        }
        if (request.getName() != null && (request.getName().length() > 250 || request.getName().length() < 2)) {
            log.error("Имя пользователя может содержать не более 250 и не менее 2 символов," +
                    " Переданное значение: {}", request.getName().length());
            validateResult.add("Имя пользователя может содержать не более 250 и не менее 2 символов," +
                    " Переданное значение: " + request.getName().length());
        }
        if (request.getEmail() != null && (request.getEmail().length() > 254 || request.getEmail().length() < 6)) {
            log.error("email пользователя может содержать не более 254 и не менее 6 символов," +
                    " Переданное значение: {}", request.getEmail().length());
            validateResult.add("email пользователя может содержать не более 254 и не менее 2 символов," +
                    " Переданное значение: " + request.getEmail().length());
        }
        if (request.getEmail() != null && request.getEmail().length() < 254) {
            String email = request.getEmail().trim();
            if (email.isBlank()) {
                throw new ValidationException("Электорнная почта состоит только из пробелов");
            }
            int index = request.getEmail().indexOf("@");
            String domenName = request.getEmail().substring(0,index);

            int lastIndex = request.getEmail().length() - 1;
            String localName = request.getEmail().substring(index,lastIndex);

            if (localName.length() > 63) {
                throw new ValidationException("Локальная часть не может превышать 64 символа");
            }
            if (domenName.length() > 64) {
                throw new ValidationException("Доменая часть часть не может превышать 64 символа");
            }
        }
    }
}
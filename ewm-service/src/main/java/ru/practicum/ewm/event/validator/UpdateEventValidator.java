package ru.practicum.ewm.event.validator;

import lombok.extern.slf4j.Slf4j;
import ru.practicum.ewm.event.model.UpdateEventRequest;
import ru.practicum.ewm.validator.AbstractValidator;

@Slf4j
public class UpdateEventValidator extends AbstractValidator {
    protected final UpdateEventRequest request;

    public UpdateEventValidator(UpdateEventRequest request) {
        this.request = request;
    }

    public void validate() {
        log.debug("validate({})",request);
        if (request.getAnnotation() != null && (request.getAnnotation().length() > 2000
                                                || request.getAnnotation().length() < 20)) {
            log.error("Краткое описание события может содержать не более 2000 и не менее 20 символа," +
                    " Переданное значение: {}", request.getAnnotation().length());
            validateResult.add("Краткое описание событи может содержать не более 2000 и не менее 20 символа," +
                    " Переданное значение: " + request.getAnnotation().length());
        }
        if (request.getCategory() != null && request.getCategory() < 1) {
            log.error("id категории к которой относится событие не может быт меньше 1, Переданное значение: {}",
                    request.getAnnotation());
            validateResult.add("id категории к которой относится событие не может быт меньше 1, Переданное значение: " +
                    request.getAnnotation());
        }
        if (request.getDescription() != null && (request.getDescription().length() > 7000
                                                 || request.getDescription().length() < 20)) {
            log.error("Полное описание события может содержать не более 7000 и не менее 20 символа," +
                    " Переданное значение: {}", request.getDescription().length());
            validateResult.add("Полное описание события может содержать не более 2000 и не менее 20 символа," +
                    " Переданное значение: " + request.getDescription().length());
        }
        if (request.getTitle() != null && (request.getTitle().length() > 120
                                           || request.getTitle().length() < 3)) {
            log.error("Заголовок события не может содеражть, больше 120 и меньше 3 символов," +
                    "  Переданное значение: {}", request.getTitle());
            validateResult.add("Заголовок события не может содеражть, больше 120 и меньше 3 символов," +
                    " Переданное значение: " + request.getTitle());
        }
        if (request.getParticipantLimit() != null && request.getParticipantLimit() < 0) {
            log.error("Количество участников не может быть меньше 0," +
                    " Переданное значение: {}", request.getParticipantLimit());
            validateResult.add("Количество участников не может быть меньше 0," +
                    " Переданное значение:" + request.getParticipantLimit());
        }
    }
}
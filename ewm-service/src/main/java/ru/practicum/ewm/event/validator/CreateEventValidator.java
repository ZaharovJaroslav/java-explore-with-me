package ru.practicum.ewm.event.validator;

import lombok.extern.slf4j.Slf4j;
import ru.practicum.ewm.event.dto.NewEventDto;
import ru.practicum.ewm.validator.AbstractValidator;

@Slf4j
public class CreateEventValidator extends AbstractValidator {

    protected final NewEventDto request;

    public CreateEventValidator(NewEventDto request) {
        this.request = request;
    }

    public void validate() {
        log.debug("validate({})",request);
        if (request.getAnnotation() == null || request.getAnnotation().isBlank()) {
            log.error("Не задано Краткое описание события");
            validateResult.add("Не задано Краткое описание события");
        }
        if (request.getAnnotation() != null && (request.getAnnotation().length() > 2000
                                                || request.getAnnotation().length() < 20)) {
            log.error("Краткое описание события может содержать не более 2000 и не менее 20 символа," +
                      " Переданное значение: {}", request.getAnnotation().length());
            validateResult.add("Краткое описание событи может содержать не более 2000 и не менее 20 символа," +
                               " Переданное значение: " + request.getAnnotation().length());
        }
        if (request.getCategory() == null) {
            log.error("Не задано id категории к которой относится событие");
            validateResult.add("id категории к которой относится событие");
        }
        if (request.getCategory() != null && request.getCategory() < 1) {
            log.error("id категории к которой относится событие не может быт меньше 1, Переданное значение: {}",
                      request.getAnnotation());
            validateResult.add("id категории к которой относится событие не может быт меньше 1, Переданное значение: " +
                    request.getAnnotation());
        }
        if (request.getDescription() == null || request.getDescription().isBlank()) {
            log.error("Не задано Полное описание события, Переданное значение: {}", request.getDescription());
            validateResult.add("Не задано Полное описание события, Переданное значение: " + request.getDescription());
        }

        if (request.getDescription() != null && (request.getDescription().length() > 7000 ||
                                                request.getDescription().length() < 20)) {
            log.error("Полное описание события может содержать не более 7000 и не менее 20 символа," +
                     " Переданное значение: {}", request.getDescription().length());
            validateResult.add("Полное описание события может содержать не более 2000 и не менее 20 символа," +
                    " Переданное значение: " + request.getDescription().length());
        }
        if (request.getEventDate() == null || request.getEventDate().isBlank()) {
            log.error("Не указано Дата и время на которые намечено событие");
            validateResult.add("Не указано Дата и время на которые намечено событие");
        }
        if (request.getLocation() == null) {
            log.error("Не задана Широта и долгота места проведения события");
            validateResult.add("Не задана Широта и долгота места проведения события");
        }
        if (request.getLocation().getLat() == null || request.getLocation().getLat() == 0) {
            log.error("Не задана Широта места проведения события, Переданное значение: {}",
                      request.getLocation().getLat());
            validateResult.add("Не задана Широта места проведения события, Переданное значение: " +
                    request.getLocation().getLat());
        }
        if (request.getLocation().getLat() == null || request.getLocation().getLon() == 0) {
            log.error("Не задана Долгота места проведения события, Переданное значение: {}",
                      request.getLocation().getLon());
            validateResult.add("Не задана Долгота места проведения события, Переданное значение: " +
                    request.getLocation().getLon());
        }

        if (request.getTitle() == null || request.getTitle().isBlank()) {
            log.error("Не задан Заголовок события, Переданное значение: {}", request.getTitle());
            validateResult.add("Не задан Заголовок события, Переданное значение: " + request.getTitle());
        }
        if (request.getTitle() != null && (request.getTitle().length() > 120 || request.getTitle().length() < 3)) {
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
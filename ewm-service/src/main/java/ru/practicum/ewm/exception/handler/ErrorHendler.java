package ru.practicum.ewm.exception.handler;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.ewm.category.controller.CategoryAdminController;
import ru.practicum.ewm.category.controller.CategoryPublicController;
import ru.practicum.ewm.event.controller.CompilationAdminController;
import ru.practicum.ewm.event.controller.CompilationPublicController;
import ru.practicum.ewm.event.controller.EventAdminController;
import ru.practicum.ewm.event.controller.EventPrivateController;
import ru.practicum.ewm.event.controller.EventPublicController;
import ru.practicum.ewm.event.controller.ParticipationRequestPrivateController;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.exception.ForbiddenException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.exception.ValidationException;
import ru.practicum.ewm.user.controller.UserController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestControllerAdvice(assignableTypes = {UserController.class,
                                         CategoryAdminController.class,
                                         CategoryPublicController.class,
                                         EventPrivateController.class,
                                         EventPublicController.class,
                                         EventAdminController.class,
                                         CompilationAdminController.class,
                                         CompilationPublicController.class,
                                         ParticipationRequestPrivateController.class})
public class ErrorHendler {
    static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponseValidator handleValidationException(final ValidationException e) {
        return new ErrorResponseValidator(HttpStatus.BAD_REQUEST.toString(),
                                         e.getMessage(),
                                         e.getErrors(),
                                         LocalDateTime.now().format(FORMATTER));
    }


    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFoundException(NotFoundException e) {
        return new ErrorResponse(HttpStatus.NOT_FOUND.toString(),
                          "Не найдено:",
                                 e.getMessage(),
                                 LocalDateTime.now().format(FORMATTER));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleConflictException(ConflictException e) {
        return new ErrorResponse(HttpStatus.CONFLICT.toString(),
                          "Конфликт данных:",
                                 e.getMessage(),
                                 LocalDateTime.now().format(FORMATTER));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleForbiddenException(ForbiddenException e) {
        return new ErrorResponse(HttpStatus.CONFLICT.toString(),
                         "Для запрошенной операции условия не выполнены.",
                                e.getMessage(),
                                LocalDateTime.now().format(FORMATTER));
    }
}
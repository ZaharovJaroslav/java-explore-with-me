package ru.practicum.ewm.exception;

public class ObjectAlreadyExistException extends RuntimeException {
    public ObjectAlreadyExistException(String message) {
        super(message);
    }
}

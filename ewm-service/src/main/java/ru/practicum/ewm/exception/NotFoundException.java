package ru.practicum.ewm.exception;

import java.util.ArrayList;

public class NotFoundException extends RuntimeException {
    private final ArrayList<String> errors;

    public NotFoundException(String message) {
        super(message);
        this.errors = new ArrayList<>();
    }
}
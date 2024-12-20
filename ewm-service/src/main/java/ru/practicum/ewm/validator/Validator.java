package ru.practicum.ewm.validator;

import lombok.Getter;
import java.util.ArrayList;

@Getter
public class Validator {
    private final ArrayList<String> messages;

    public Validator() {
        messages = new ArrayList<>();
    }

    public Validator(String message) {
        messages = new ArrayList<>();
        messages.add(message);
    }

    public boolean isValid() {
        return messages.isEmpty();
    }

    public void add(String message) {
        messages.add(message);
    }
}
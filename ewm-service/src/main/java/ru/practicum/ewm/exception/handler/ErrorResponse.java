package ru.practicum.ewm.exception.handler;

import lombok.Getter;

import java.util.List;

@Getter
public class ErrorResponse {
    private final String status;
    private final String reason;
    private final List<String> message;
    private final String timestamp;

    public ErrorResponse(String status, String reason, List<String> message, String timestamp) {
        this.status = status;
        this.reason = reason;
        this.message = message;
        this.timestamp = timestamp;
    }


}
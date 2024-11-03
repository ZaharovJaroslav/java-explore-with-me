package ru.practicum.ewm.request.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

import static ru.practicum.ewm.constant.Constants.DATE_FORMAT;

@Getter
@Setter
public class ParticipationRequestDto {
    private Long id;

    @JsonFormat(pattern = DATE_FORMAT)
    private LocalDateTime created;

    private Long event;

    private Long requester;

    private String status;
}
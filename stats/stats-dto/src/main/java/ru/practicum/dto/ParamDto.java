package ru.practicum.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ParamDto {
    private String startTime;
    private String endTime;
}
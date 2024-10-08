package ru.practicum.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class ParamHitDto {
    private String app;
    private String uri;
    private String ip;
    private String timestamp;
}
package ru.practicum.stats.mapper;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class DataTimeMapper {
   static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static Instant toInstant(String time) {
        LocalDateTime dateTime = LocalDateTime.parse(time,FORMATTER);
        ZonedDateTime zonedDateTime = ZonedDateTime.of(dateTime, ZoneId.of("UTC+0"));
        return Instant.from(zonedDateTime);
    }
}
package ru.practicum.stats.mapper;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import java.time.Instant;

public class DataTimeMapperTest {
    private static final  String time = "2022-09-25 00:00:00";

    @Test
    void toInstant_ShouldReturnInstant() {
        Instant result = DataTimeMapper.toInstant(time);
        Instant instant = Instant.ofEpochSecond(1664064000);

        Assertions.assertEquals(instant, result);
    }
}
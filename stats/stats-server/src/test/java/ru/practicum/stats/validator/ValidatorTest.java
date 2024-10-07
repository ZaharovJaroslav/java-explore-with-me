package ru.practicum.stats.validator;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.practicum.dto.ParamDto;
import ru.practicum.dto.ParamHitDto;

class ValidatorTest {
   private final ParamHitDto paramHitDtoIsBlank = new ParamHitDto("", "", "", "");

    private final ParamHitDto paramHitDto = new ParamHitDto("ewm-main-service",
                                               "/events/1",
                                               "192.163.0.1",
                                         "2022-09-29 15:00:23");

    private final ParamDto paramDtoIsBlank = new ParamDto("", "");

    private final ParamDto paramDto = new ParamDto("2022-09-25 00:00:00",
                                     "2022-09-29 23:00:00");

    @Test
    void createStatValidator_ShouldNotPassValidation() {
        CreateStatValidator validator = new CreateStatValidator(paramHitDtoIsBlank);
        validator.validate();
        boolean result = validator.isValid();
        Assertions.assertFalse(result);
    }

    @Test
    void createStatValidator_ShouldPassValidation() {
        CreateStatValidator validator = new CreateStatValidator(paramHitDto);
        validator.validate();
        boolean result = validator.isValid();
        Assertions.assertTrue(result);
    }

    @Test
    void cetStatsValidator_ShouldNotPassValidation() {
        GetStatsValidator validator = new GetStatsValidator(paramDtoIsBlank);
        validator.validate();
        boolean result = validator.isValid();
        Assertions.assertFalse(result);
    }

    @Test
    void cetStatsValidator_ShouldPassValidation() {
        GetStatsValidator validator = new GetStatsValidator(paramDto);
        validator.validate();
        boolean result = validator.isValid();
        Assertions.assertTrue(result);
    }
}
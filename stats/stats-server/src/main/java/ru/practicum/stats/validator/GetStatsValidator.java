package ru.practicum.stats.validator;

import ru.practicum.dto.ParamDto;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GetStatsValidator extends AbstractValidator {

   static final String TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    protected final ParamDto request;

    public GetStatsValidator(ParamDto request) {
        this.request = request;
    }

    public void validate() {
        if (request.getStartTime() == null || request.getStartTime().isBlank()) {
            log.error("Не указано Дата и время начала диапазона за который нужно выгрузить статистику");
            validateResult.add("Не указано Дата и время начала диапазона за который нужно выгрузить статистику");
        }
        if (request.getEndTime() == null || request.getEndTime().isBlank()) {
            log.error("Не указано Дата и время начала диапазона за который нужно выгрузить статистику");
            validateResult.add("Не указано Дата и время начала диапазона за который нужно выгрузить статистику");
        }
    }
}
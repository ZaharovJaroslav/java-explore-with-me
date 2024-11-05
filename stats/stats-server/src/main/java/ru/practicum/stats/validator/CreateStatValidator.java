package ru.practicum.stats.validator;

import ru.practicum.dto.ParamHitDto;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CreateStatValidator extends AbstractValidator {
    protected final ParamHitDto request;

    public CreateStatValidator(ParamHitDto request) {
        this.request = request;
    }

    public void validate() {
        log.debug("validate({})",request);
        if (request.getApp() == null || request.getApp().isBlank()) {
            log.error("Не указан Идентификатор сервиса для которого записывается информаци");
            validateResult.add("Не указан Идентификатор сервиса для которого записывается информаци");
        }
        if (request.getUri() == null || request.getUri().isBlank()) {
            log.error("Не указан URI для которого был осуществлен запрос");
            validateResult.add("Не указан URI для которого был осуществлен запрос");
        }
        if (request.getIp() == null || request.getIp().isBlank()) {
            log.error("Не указан IP-адрес пользователя, осуществившего запрос");
            validateResult.add("Не указан IP-адрес пользователя, осуществившего запрос");
        }
        if (request.getTimestamp() == null) {
            log.error("Не указано Дата и время, когда был совершен запрос к эндпоинту");
            validateResult.add("Не указано Дата и время, когда был совершен запрос к эндпоинту");
        }
    }
}

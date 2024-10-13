package ru.practicum.ewm.category.validator;

import lombok.extern.slf4j.Slf4j;
import ru.practicum.ewm.validator.AbstractValidator;
import java.util.List;

@Slf4j
public class GetCategoryValidator extends AbstractValidator {

    protected final List<Integer> requestParam;

    public GetCategoryValidator(List<Integer> requestParam) {
        this.requestParam = requestParam;
    }

    public void validate() {
        log.debug("validate({})",requestParam);
        for (Integer param :requestParam) {
            if (param < 0 ) {
                log.error("Значение не может содержать отрицательное число, Переданное значение: {}", requestParam);
                validateResult.add("Значение не может содержать отрицательное число Переданное значение: " + requestParam);
            }
        }
    }
}
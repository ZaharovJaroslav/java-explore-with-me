package ru.practicum.stats.controller;

import ru.practicum.dto.ParamHitDto;
import ru.practicum.dto.StatDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.stats.service.StatsService;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
public class StatsController {
    @Autowired
    private final StatsService statsService;

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public void addStat(@RequestBody ParamHitDto newStat) {
        log.debug("Добавление новой статистики: {}", newStat);
        statsService.addStat(newStat);
    }

    @GetMapping("/stats")
    public List<StatDto> getStats(@RequestParam String start,
                                  @RequestParam String end,
                                  @RequestParam(value = "uris", required = false) String[] uris,
                                  @RequestParam(defaultValue = "false") boolean unique) {
    log.debug("Собрать статистику с {} по {}", start, end);
    return statsService.getStats(start, end,List.of(uris), unique);
    }
}

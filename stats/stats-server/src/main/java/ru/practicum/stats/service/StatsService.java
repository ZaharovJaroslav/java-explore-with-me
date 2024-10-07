package ru.practicum.stats.service;

import ru.practicum.dto.ParamHitDto;
import ru.practicum.dto.StatDto;
import ru.practicum.stats.model.Stat;
import java.util.List;

public interface StatsService {

    List<StatDto> groupStatByLinkAndIp(List<Stat> stats, boolean unique);

    void addStat(ParamHitDto stat);

    List<StatDto> getStats(String startTime, String endTime, List<String> uris, boolean unique);
}
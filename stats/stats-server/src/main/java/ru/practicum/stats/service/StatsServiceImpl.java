package ru.practicum.stats.service;

import ru.practicum.dto.ParamDto;
import ru.practicum.dto.ParamHitDto;
import ru.practicum.dto.StatDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.stats.mapper.DataTimeMapper;
import ru.practicum.stats.exception.ValidationException;
import ru.practicum.stats.model.Stat;
import ru.practicum.stats.repository.StatsRepository;
import ru.practicum.stats.validator.CreateStatValidator;
import ru.practicum.stats.validator.GetStatsValidator;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class StatsServiceImpl implements StatsService {
    @Autowired
    private final StatsRepository statsRepository;

    @Override
    public void addStat(ParamHitDto newStat) {
        log.debug("addStat({})", newStat);
        CreateStatValidator validator = new CreateStatValidator(newStat);
        validator.validate();
        if (!validator.isValid()) {
            throw new ValidationException("Невалидные параметры", validator.getMessages());
        }
        Stat stat = Stat.builder()
                .app(newStat.getApp())
                .uri(newStat.getUri())
                .ip(newStat.getIp())
                .timestamp(DataTimeMapper.toInstant(newStat.getTimestamp()))
                .build();
        statsRepository.save(stat);
    }

    @Override
    public List<StatDto> groupStatByLinkAndIp(List<Stat> stats, boolean unique) {
        log.debug("groupingStatsByLinkAndUnique({},{})", stats, unique);
        List<StatDto> statForOutput = new ArrayList<>();
        Map<String, List<Stat>> groupedStata = stats.stream()
                .collect(Collectors.groupingBy(Stat::getUri));

        for (Map.Entry<String, List<Stat>> stat : groupedStata.entrySet()) {
            String key = stat.getKey();
            long count = stat.getValue().size();
            Stat curreentStat = stat.getValue().getFirst();
            if (unique) {
                Set<String> uniqueIp =  stat.getValue().stream()
                        .map(Stat::getIp)
                        .collect(Collectors.toSet());
                count = uniqueIp.size();
            }
            StatDto statDto = StatDto.builder()
                    .app(curreentStat.getApp())
                    .uri(key)
                    .hits(count)
                    .build();
            statForOutput.add(statDto);
        }
        return statForOutput;
    }

    @Override
    public List<StatDto> getStats(String startTime, String endTime, List<String> uris, boolean unique) {
        log.debug("getStats({},{},{},{})", startTime, endTime, uris,unique);
        GetStatsValidator validator = new GetStatsValidator(new ParamDto(startTime,endTime));
        validator.validate();
        if (!validator.isValid()) {
            throw new ValidationException("Невалидные параметры", validator.getMessages());
        }
        List<StatDto> statForOutput;
        List<Stat> stats;
        if (uris == null) {
            stats = statsRepository.findStatByForThePeriod(DataTimeMapper.toInstant(startTime),
                    DataTimeMapper.toInstant(endTime));
        } else {
            stats = statsRepository.findStatByUriForThePeriod(DataTimeMapper.toInstant(startTime),
                    DataTimeMapper.toInstant(endTime),
                    uris);
        }
        statForOutput = groupStatByLinkAndIp(stats, unique);

        return statForOutput;
    }
}

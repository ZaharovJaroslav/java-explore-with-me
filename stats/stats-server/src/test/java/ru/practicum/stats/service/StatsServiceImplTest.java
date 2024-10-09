package ru.practicum.stats.service;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.practicum.dto.StatDto;
import ru.practicum.stats.model.Stat;
import ru.practicum.stats.repository.StatsRepository;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = {StatsServiceImpl.class})
class StatsServiceImplTest {

    @Autowired StatsService statsService;
    @MockBean StatsRepository repository;

    private final  Stat stat = new Stat(1L, "ewm-main-service", "/events/1", "192.163.0.1",
                                                                        Instant.ofEpochSecond(1664409600));
    private final Stat stat2 = new Stat(2L, "eame-main-service", "/events/1", "192.163.0.2",
                                                                          Instant.ofEpochSecond(1664409600));
    private final Stat stat3 = new Stat(3L, "ewm-main-service", "/events/1", "192.163.0.3",
                                                                         Instant.ofEpochSecond(1664409600));
    private final StatDto statDto = new StatDto("ewm-main-service", "/events/1", 1L);

    private final StatDto statDto2 = new StatDto("ewm-main-service", "/events/1", 3L);

    @Test
    void addStat_ShouldSaveStatsInRepository() {
        repository.save(stat);
        when(repository.findById(any())).thenReturn(Optional.of(stat));
        Optional<Stat> actualOptional = repository.findById(1L);
        Stat resultActual = actualOptional.get();

        Assertions.assertEquals(stat, resultActual);
        Assertions.assertEquals(stat.getId(), resultActual.getId());
        Assertions.assertEquals(stat.getIp(), resultActual.getIp());
        Assertions.assertEquals(stat.getApp(), resultActual.getApp());
        Assertions.assertEquals(stat.getTimestamp(), resultActual.getTimestamp());
    }

    @Test
    void groupStatByLinkAndIp_ShouldReturnAllStats() {
        when(statsService.groupStatByLinkAndIp(
                repository.findStatByForThePeriod(any(), any()),false))
                .thenReturn(List.of(statDto));
        List<StatDto> actual = statsService.groupStatByLinkAndIp(List.of(stat), false);
        List<StatDto> expected = List.of(statDto);

        Assertions.assertEquals(expected, actual);
        Assertions.assertEquals(1, expected.size());
    }

    @Test
    void groupStatByLinkAndIp_ShouldReturnStatsByUriAndUniqueIP() {
        when(statsService.groupStatByLinkAndIp(
                repository.findStatByUriForThePeriod(any(), any(), anyList()), true))
                .thenReturn(List.of(statDto));
        List<StatDto> actual = statsService.groupStatByLinkAndIp(List.of(stat,stat2,stat3), true);
        List<StatDto> expected = List.of(statDto2);

        Assertions.assertEquals(expected, actual);
        Assertions.assertEquals(1, expected.size());
    }
}
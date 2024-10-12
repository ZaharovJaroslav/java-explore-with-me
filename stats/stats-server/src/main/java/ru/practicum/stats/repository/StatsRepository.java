package ru.practicum.stats.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.stats.model.Stat;
import java.time.Instant;
import java.util.List;

@Repository
public interface StatsRepository extends JpaRepository<Stat, Long> {

    @Query("SELECT s FROM Stat s "
            + "WHERE s.uri IN (:uris) "
            + "AND timestamp between :start AND :end ")
    List<Stat> findStatByUriForThePeriod(Instant start, Instant end, List<String> uris);

    @Query("SELECT s FROM Stat s "
            + "WHERE timestamp between :start AND :end ")
    List<Stat> findStatByForThePeriod(Instant start, Instant end);
}
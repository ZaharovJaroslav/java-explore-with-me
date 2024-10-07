package ru.practicum.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import ru.practicum.dto.ParamHitDto;
import ru.practicum.dto.StatDto;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Component
public class StatsClient {
    static final String STATS_SERVER = "${stats-server.url}";
    private final RestTemplate restTemplate;

    public void save(ParamHitDto newStat) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<ParamHitDto> requestEntity = new HttpEntity<>(newStat,httpHeaders);
        restTemplate.exchange(STATS_SERVER + "/hit", HttpMethod.POST, requestEntity, ParamHitDto.class);
    }

    public ResponseEntity<List<StatDto>> getStats(String start, String end, String[]uris, boolean unique) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<?> requestEntity = new HttpEntity<>(httpHeaders);

        Map<String, Object> uriVariables = Map.of(
                "start", start,
                "end", end,
                "uris", uris,
                "unique", unique);

        String uri = STATS_SERVER + "/stats?start={start}&end={end}&uris={uris}&unique={unique}";

        return restTemplate.exchange(uri, HttpMethod.GET, requestEntity,
                new ParameterizedTypeReference<List<StatDto>>() {
                },
                uriVariables);
    }
}
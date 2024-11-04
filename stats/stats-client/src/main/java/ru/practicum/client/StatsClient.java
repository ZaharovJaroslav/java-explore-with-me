package ru.practicum.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import ru.practicum.dto.ParamHitDto;
import ru.practicum.dto.StatDto;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class StatsClient {

    String statsServer = "http://stats-service:9090";
    // String statsServer = "http://localhost:9090";
    private final RestTemplate restTemplate;


    public void save(ParamHitDto newStat) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<ParamHitDto> requestEntity = new HttpEntity<>(newStat,httpHeaders);
        restTemplate.exchange(statsServer + "/hit", HttpMethod.POST, requestEntity, ParamHitDto.class);
    }

    public List<StatDto> getStats(String start, String end, List<String> uris, boolean unique) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<?> requestEntity = new HttpEntity<>(httpHeaders);

        Map<String, Object> uriVariables = Map.of(
                "start", start,
                "end", end,
                "uris", uris,
                "unique", unique);

        String uri = statsServer + "/stats?start={start}&end={end}&uris={uris}&unique={unique}";
        return restTemplate.exchange(uri, HttpMethod.GET, requestEntity,
                new ParameterizedTypeReference<List<StatDto>>() {
                },
                uriVariables).getBody();
    }
}
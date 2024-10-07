package ru.practicum.stats.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.dto.ParamHitDto;
import ru.practicum.dto.StatDto;
import ru.practicum.stats.model.Stat;
import ru.practicum.stats.service.StatsService;
import java.time.Instant;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(StatsController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@AutoConfigureMockMvc
class StatsControllerTest {
    @MockBean
    StatsService statsService;

    private final ObjectMapper objectMapper;
    private final MockMvc mockMvc;

    private final StatDto statDto = new StatDto("ewm-main-service", "/events/1", 1L);
    private final Stat stat = new Stat(1L, "ewm-main-service", "/events/1", "192.163.0.1",
            Instant.ofEpochSecond(1664409600));

    private final ParamHitDto statForCreated = new ParamHitDto("ewm-main-service",
            "/events/1",
            "192.163.0.1",
            "2022-09-06 11:00:23");

    @Test
    void addStat_ShouldReturnIsCreated() throws Exception {
        statsService.addStat(statForCreated);
        mockMvc.perform(post("/hit")
                        .content(new ObjectMapper().writeValueAsString(statForCreated))
                        .contentType(MediaType.APPLICATION_JSON))

                .andExpect(status().isCreated());
    }

    @Test
    void getStat() throws Exception {
        when(statsService.getStats(anyString(), anyString(), anyList(), anyBoolean()))
                .thenReturn(List.of(statDto));
        String result = mockMvc.perform(get("/stats")
                        .param("start", "2022-09-25 00:00:00")
                        .param("end", "2022-09-29 23:00:00")
                        .param("uris", "/events/1")
                        .param("unique", "false")
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(List.of(statDto)), result);
    }
}
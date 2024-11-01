package ru.practicum.ewm.event.utill;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import ru.practicum.client.StatsClient;

@Configuration
public class AppConfig {
    @Bean
    public StatsClient statsClient() {
        return new StatsClient(new RestTemplate());
    }
}

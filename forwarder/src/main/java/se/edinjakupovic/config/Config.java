package se.edinjakupovic.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.reactive.function.client.WebClient;
import se.edinjakupovic.adapters.db.PostgresEventRepository;
import se.edinjakupovic.adapters.reporter.ScheduledEventReporter;
import se.edinjakupovic.core.ServiceImplementation;
import se.edinjakupovic.core.ports.EventReporter;
import se.edinjakupovic.core.ports.EventRepository;
import se.edinjakupovic.core.ports.EventService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
@EnableScheduling
public class Config {

    @Bean
    public EventRepository eventRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        return new PostgresEventRepository(jdbcTemplate);
    }

    @Bean
    public EventService eventService(EventRepository eventRepository) {
        return new ServiceImplementation(eventRepository);
    }

    @Bean
    @ConditionalOnProperty("scheduling.enabled")
    public EventReporter scheduledEventReporter(EventRepository eventRepository,
                                                @Value("${receiver.host}") String host,
                                                @Value("${receiver.port}") int port,
                                                @Value("${receiver.uripath}") String uriPath) {
        var client = WebClient.builder()
                .baseUrl("%s:%d".formatted(host, port))
                .build();
        return new ScheduledEventReporter(eventRepository, client, uriPath);
    }

}

package se.edinjakupovic.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.reactive.function.client.WebClient;
import se.edinjakupovic.adapters.db.PostgresEventRepository;
import se.edinjakupovic.adapters.reporter.ReceiverClient;
import se.edinjakupovic.adapters.reporter.ScheduledEventReporter;
import se.edinjakupovic.adapters.reporter.Sender;
import se.edinjakupovic.core.ServiceImplementation;
import se.edinjakupovic.core.ports.EventRepository;
import se.edinjakupovic.core.ports.EventSender;
import se.edinjakupovic.core.ports.EventService;


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
    public ReceiverClient receiverClient(
            @Value("${receiver.baseurl}") String baseurl,
            @Value("${receiver.uripath}") String uriPath) {
        var client = WebClient.builder()
                .baseUrl(baseurl)
                .build();
        return new ReceiverClient(client, uriPath);
    }

    @Bean
    public EventSender eventSender(EventRepository eventRepository, ReceiverClient receiverClient) {
        return new Sender(receiverClient, eventRepository);
    }

    @Bean
    @ConditionalOnProperty("scheduling.enabled")
    public ScheduledEventReporter scheduledEventReporter(EventSender eventReporter,
                                                         EventRepository eventRepository) {
        return new ScheduledEventReporter(eventReporter, eventRepository);
    }

}

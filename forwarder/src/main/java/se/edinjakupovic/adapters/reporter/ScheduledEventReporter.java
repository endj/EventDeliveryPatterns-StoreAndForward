package se.edinjakupovic.adapters.reporter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import se.edinjakupovic.core.Event;
import se.edinjakupovic.core.EventReportResult;
import se.edinjakupovic.core.ports.EventReporter;
import se.edinjakupovic.core.ports.EventRepository;

import java.util.Optional;
import java.util.UUID;

import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
public class ScheduledEventReporter  implements EventReporter {

    private final EventRepository eventRepository;
    private final WebClient webClient;
    private final String reportUri;

    public ScheduledEventReporter(EventRepository eventRepository, WebClient webClient, String reportUri) {
        this.eventRepository = eventRepository;
        this.webClient = webClient;
        this.reportUri = reportUri;
    }

    @Override
    public EventReportResult forwardEvent(Event event) {
        log.info("Sending event {}", event);
        try {
            ResponseEntity<Void> block = webClient.post()
                    .uri(reportUri)
                    .body(BodyInserters.fromValue(event))
                    .header(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                    .retrieve()
                    .toEntity(Void.class)
                    .block();
            if(block.getStatusCode().is2xxSuccessful())
                return new EventReportResult(true);
            else
                return new EventReportResult(false);
        } catch (Exception e) {
            log.warn("Got exception while reporting", e);
            return new EventReportResult(false);
        }
    }


    @Scheduled(fixedDelay = 5000)
    public void proccess() {
        Optional<Event> nextUnprocessed = eventRepository.getNextUnprocessed();
        if(nextUnprocessed.isPresent()) {
            var event = nextUnprocessed.get();
            log.info("Got event to process {}", event);
            EventReportResult eventReportResult = forwardEvent(event);
            if(eventReportResult.isSuccess())
                setToProcessed(event.getId());
            else
                setToFailed(event.getId());
        }
    }

    @Override
    public void setToProcessed(UUID id) {
        eventRepository.setToProcessed(id);
    }

    @Override
    public void setToFailed(UUID id) {
        eventRepository.setToFailed(id);
    }
}

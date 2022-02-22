package se.edinjakupovic.adapters.reporter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import se.edinjakupovic.core.Event;
import se.edinjakupovic.core.EventReportResult;

import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
public class ReceiverClient {
    private final WebClient webClient;
    private final String reportUri;

    public ReceiverClient(WebClient webClient, String reportUri) {
        this.webClient = webClient;
        this.reportUri = reportUri;
    }

    public EventReportResult sendEvent(Event event) {
        log.info("Sending event {}", event);
        try {
            ResponseEntity<Void> block = webClient.post()
                    .uri(reportUri)
                    .body(BodyInserters.fromValue(event))
                    .header(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                    .retrieve()
                    .toEntity(Void.class)
                    .block();
            return new EventReportResult(block.getStatusCode().is2xxSuccessful());
        } catch (WebClientResponseException e) {
            return new EventReportResult(false);
        } catch (Exception e) {
            log.warn("Got exception while reporting", e);
            throw e;
        }
    }
}

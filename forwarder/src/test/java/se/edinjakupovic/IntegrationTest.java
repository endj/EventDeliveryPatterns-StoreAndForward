package se.edinjakupovic;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import se.edinjakupovic.core.Event;
import se.edinjakupovic.core.EventToSend;
import se.edinjakupovic.core.ports.EventRepository;
import se.edinjakupovic.core.ports.EventSender;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.IntStream;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;
import static se.edinjakupovic.WireMockInitializer.WIRE_MOCK;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {
        "scheduling.enabled=false"
})
@ContextConfiguration(initializers = {PostgresContainer.class, WireMockInitializer.class})
class IntegrationTest {

    @LocalServerPort
    private int port;

    @Value("${receiver.uripath}")
    String uriPath;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    EventSender sender;

    @Autowired
    EventRepository eventRepository;

    @BeforeEach
    void reset() {
        PostgresContainer.reset();
    }

    @Test
    void canReceiveAndPersistEvent() {
        ResponseEntity<Event> result = sendEvent();

        assertThat(result.getStatusCode().is2xxSuccessful()).isTrue();
    }

    @Test
    void canRetryOnFailure() {
        var event = new Event("some data", UUID.randomUUID());
        var result = sendEvent(event);
        assertThat(result.getStatusCode().is2xxSuccessful()).isTrue();

        EventToSend eventToSend = eventRepository.getNextProcessable().orElseThrow();
        WIRE_MOCK.stubFor(post(urlMatching(uriPath))
                .willReturn(aResponse().withStatus(500)));

        var eventReportResult = sender.sendEvent(eventToSend);
        assertThat(eventReportResult.isSuccess()).isFalse();

        WIRE_MOCK.stubFor(post(urlMatching(uriPath))
                .willReturn(aResponse().withStatus(200)));

        moveLastProcessingTimeBack11minutes(event);

        EventToSend eventToSendRetry = eventRepository.getNextProcessable().orElseThrow();
        var retryResult = sender.sendEvent(eventToSendRetry);

        assertThat(retryResult.isSuccess()).isTrue();
    }

    @Test
    void canTryAgainAfter10min() {
        var event = new Event("some data", UUID.randomUUID());
        var result = sendEvent(event);
        assertThat(result.getStatusCode().is2xxSuccessful()).isTrue();

        assertThat(eventRepository.getNextProcessable()).isPresent();
        assertThat(eventRepository.getNextProcessable()).isEmpty();

        moveLastProcessingTimeBack11minutes(event);

        assertThat(eventRepository.getNextProcessable()).isPresent();
    }


    @Test
    void shouldOnlyAllow3Attempts() {
        var event = new Event("some data", UUID.randomUUID());
        var result = sendEvent(event);

        IntStream.range(0, 3).forEach(iteration -> {
            eventRepository.getNextProcessable().orElseThrow();
            moveLastProcessingTimeBack11minutes(event);
        });


        assertThat(eventRepository.getNextProcessable()).isEmpty();
    }

    @Test
    void shouldFailAfter3Attempts() {
        var event = new Event("data", UUID.randomUUID());
        var eventResponseEntity = sendEvent(event);

        WIRE_MOCK.stubFor(post(urlMatching(uriPath))
                .willReturn(aResponse().withStatus(500)));

        IntStream.range(0, 3).forEach(iteration -> {
            EventToSend eventToSend = eventRepository.getNextProcessable().orElseThrow();
            sender.sendEvent(eventToSend);
            moveLastProcessingTimeBack11minutes(event);
        });
        assertThat(isFailed(event)).isTrue();
    }


    @Test
    void shouldNotAllowSameEventToBePickedUpConcurrently() throws Exception {
        var eventCount = new ConcurrentHashMap<UUID, Integer>();
        IntStream.range(0, 1000)
                .parallel()
                .forEach(i -> sendEvent(new Event("" + i, UUID.randomUUID())));

        var done = new AtomicBoolean(false);
        var pool = Executors.newFixedThreadPool(30);
        IntStream.range(0, 30)
                .mapToObj(i -> (Runnable) () -> {
                    while (!done.get())
                        eventRepository.getNextProcessable()
                                .ifPresent(event ->
                                        eventCount.compute(event.getEvent().getId(), (k, v) -> v == null ? 1 : v + 1));
                }).forEach(pool::execute);

        System.out.println("Wait a while");
        Thread.sleep(5000);
        done.compareAndSet(false, true);

        Optional<Integer> doubleReported = eventCount.values().stream().filter(c -> c > 1)
                .findAny();
        Assertions.assertThat(doubleReported).isEmpty();
    }


    private boolean isFailed(Event event) {
        return PostgresContainer.template.query("""
                                SELECT failed_at
                                FROM events
                                WHERE events.external_id = :external_id
                                """, Map.of("external_id", event.getId().toString()),
                        (rs, rowNum) -> rs.getString("failed_at"))
                       .size() == 1;
    }

    private void moveLastProcessingTimeBack11minutes(Event event) {
        PostgresContainer.template.update("""
                UPDATE events
                SET processing_at = now() - '11 minutes'::interval
                WHERE events.external_id = :external_id
                """, Map.of("external_id", event.getId().toString()));
    }


    private ResponseEntity<Event> sendEvent() {
        return sendEvent(new Event("some data", UUID.randomUUID()));
    }

    private ResponseEntity<Event> sendEvent(Event event) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        var entity = new HttpEntity<>(event, headers);
        return this.restTemplate.postForEntity(
                "http://localhost:%d/forwarder/event".formatted(port)
                , entity, Event.class);
    }

}


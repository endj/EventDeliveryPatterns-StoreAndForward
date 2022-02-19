package se.edinjakupovic;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
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

import java.util.UUID;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {
        "scheduling.enabled=false"
})
@ContextConfiguration(initializers = PostgresContainer.class)
class IntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @BeforeEach
    void reset() {
        PostgresContainer.reset();
    }

    @Test
    void canReceiveAndPersistEvent() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        var event = new Event("some data", UUID.randomUUID());
        var entity = new HttpEntity<Event>(event, headers);

        ResponseEntity<Event> result = this.restTemplate.postForEntity(
                "http://localhost:%d/forwarder/event".formatted(port)
                , entity, Event.class);

        Assertions.assertThat(result.getStatusCode().is2xxSuccessful()).isTrue();
    }

}


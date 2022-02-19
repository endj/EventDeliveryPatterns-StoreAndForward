package se.edinjakupovic;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import se.edinjakupovic.core.Event;

import java.util.UUID;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class IntegrationTest {

    // TODO replace types with values
    ;

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void canSendRequest() {
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


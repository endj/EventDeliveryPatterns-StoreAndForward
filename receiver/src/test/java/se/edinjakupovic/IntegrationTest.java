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


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class IntegrationTest {

    // TODO replace types with values
    String request = """
           {
  "id":"UUID",
  "data":"String"
}


            """;

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void canSendRequest() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<>(request, headers);

        ResponseEntity<String> result = this.restTemplate.postForEntity(
                "http://localhost:%d/event-receiver/event".formatted(port)
                , entity, String.class);

        Assertions.assertThat(result.getStatusCode().is2xxSuccessful()).isTrue();
    }

}


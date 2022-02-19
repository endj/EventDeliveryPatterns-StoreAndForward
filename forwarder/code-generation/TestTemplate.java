public  class TestTemplate {

    private static final String REQUEST_TEMPLATE = " @{request}@";

    private static final String TEMPLATE = """
            package @{basepackage}@;
                        
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
                String request = ""\"
                        @{request}@
                        ""\";
                        
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
                            "http://localhost:%d/@{serviceName}@/@{classNameLower}@".formatted(port)
                            , entity, String.class);
                        
                    Assertions.assertThat(result.getStatusCode().is2xxSuccessful()).isTrue();
                }
                        
            }
                        
            """;

    public static String generateIntegrationTestCode(Configuration configuration, String requestModel, String className) {
        return TEMPLATE.replace(TemplateVariables.BASE_PACKAGE_PATH_TEMPLATE, FileManager.BASE_IMPORT_PACKAGE)
                .replace(TemplateVariables.CLASS_LOWER_TEMPLATE, TextTransformer.firstLowerCase(className))
                .replace(TemplateVariables.SERVICE_NAME_TEMPLATE, configuration.serviceName())
                .replace(REQUEST_TEMPLATE, requestModel);
    }
}

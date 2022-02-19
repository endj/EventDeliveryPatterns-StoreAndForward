package se.edinjakupovic;

import org.checkerframework.checker.units.qual.C;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.PostgisContainerProvider;

import java.util.Map;

public class PostgresContainer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    private static final JdbcDatabaseContainer<?> CONTAINER = createContainer();
    private static JdbcTemplate template;
    static {
        start();
    }

    static void start() {
        CONTAINER.start();
        var datasource = new SimpleDriverDataSource(CONTAINER.getJdbcDriverInstance(),
                CONTAINER.getJdbcUrl(), CONTAINER.getUsername(), CONTAINER.getPassword());
        template = new JdbcTemplate(datasource);
    }

    static JdbcDatabaseContainer<?> createContainer() {
        try(JdbcDatabaseContainer<?> container = (JdbcDatabaseContainer<?>) new PostgisContainerProvider().newInstance()
                .withUsername("postgres")
                .withPassword("")
                .withEnv("POSTGRES_HOST_AUTH_METHOD","trust")) {
            return container;
        }
    }

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        TestPropertyValues.of(
                Map.of("spring.datasource.url", CONTAINER.getJdbcUrl(),
                        "spring.datasource.username", CONTAINER.getUsername())
        ).applyTo(applicationContext);
    }

    public static void reset() {
        template.update("DELETE FROM EVENTS");
        // template.update()
    }
}

package se.edinjakupovic;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class ForwarderApplication implements CommandLineRunner {

    @Autowired
    JdbcTemplate template;

    public static void main(String[] args) {
        SpringApplication.run(ForwarderApplication.class, args);
    }

    @Override
    public void run(String... args) {
        template.execute("""
                CREATE TABLE IF NOT EXISTS events (
                    id serial PRIMARY KEY,
                    external_id text NOT NULL,
                    data text NOT NULL,
                    created_at timestamp,
                    processing_at timestamp,
                    processed_at timestamp,
                    failed_at timestamp,
                    attempts integer DEFAULT 0
                );
                                
                CREATE UNIQUE INDEX IF NOT EXISTS event_unique on events(data);
                """);
    }
}


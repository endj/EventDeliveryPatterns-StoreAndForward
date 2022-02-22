package se.edinjakupovic.adapters.db;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.transaction.annotation.Transactional;
import se.edinjakupovic.core.Event;
import se.edinjakupovic.core.EventResponse;
import se.edinjakupovic.core.EventToSend;
import se.edinjakupovic.core.ports.EventRepository;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Slf4j
public class PostgresEventRepository implements EventRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public PostgresEventRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public EventResponse save(Event event) {
        try {
            int update = jdbcTemplate.update("""
                    INSERT INTO events(external_id, data, created_at)
                    VALUES(:id, :data, now())
                    ON CONFLICT DO NOTHING
                    """, Map.of("id", event.getId().toString(),
                    "data", event.getData()));
            if (update == 1)
                log.info("Persisted event {}", event);
            return new EventResponse(update == 1);
        } catch (Exception e) {
            log.warn("Got exception while saving {}", event);
            return new EventResponse(false);
        }
    }

    @Transactional
    @Override
    public Optional<EventToSend> getNextProcessable() {
        return jdbcTemplate.query("""
                 WITH event_to_report AS (SELECT
                        id,
                        external_id,
                        data,
                        attempts
                 FROM events 
                 WHERE (processing_at is NULL OR processing_at < now() - '10 minutes'::interval) AND 
                       processed_at is NULL AND 
                       failed_at is NULL  AND
                       attempts < 3
                 ORDER BY created_at 
                 LIMIT 1 FOR UPDATE SKIP LOCKED )
                 UPDATE events 
                 SET processing_at = now(),
                     attempts = event_to_report.attempts + 1
                 FROM event_to_report
                 WHERE events.id = event_to_report.id
                 RETURNING 
                    event_to_report.data, 
                    event_to_report.external_id,
                    event_to_report.attempts;
                """, new EventRowMapper()).stream().findFirst();
    }

    @Override
    public void setToProcessed(UUID id) {
        log.info("Processed event with id {}", id);
        jdbcTemplate.update("""
                UPDATE events
                SET processed_at = now()
                WHERE events.external_id = :external_id
                """, Map.of("external_id", id.toString()));
    }

    @Override
    public void setToFailed(UUID id) {
        jdbcTemplate.update("""
                UPDATE events
                SET failed_at = now()
                WHERE events.external_id = :external_id
                """, Map.of("external_id", id.toString()));
    }

}

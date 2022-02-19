package se.edinjakupovic.adapters.db;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.transaction.annotation.Transactional;
import se.edinjakupovic.core.EventResponse;
import se.edinjakupovic.core.ports.EventRepository;
import se.edinjakupovic.core.Event;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class PostgresEventRepository implements EventRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public PostgresEventRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public EventResponse save(Event event) {
        int update = jdbcTemplate.update("""
                INSERT INTO events(external_id, data, created_at)
                VALUES(:id, :data, now())
                ON CONFLICT DO NOTHING
                """, Map.of("id", event.getId().toString(),
                "data", event.getData()));
        return new EventResponse(update == 1);
    }

    @Transactional
    @Override
    public Optional<Event> getNextUnprocessed() {
        return jdbcTemplate.query("""
                 WITH event_to_report AS (SELECT
                        id,
                        external_id,
                        data
                 FROM events 
                 WHERE processing_at is NULL AND 
                       processed_at is NULL AND 
                       failed_at is NULL 
                 ORDER BY created_at 
                 LIMIT 1 FOR UPDATE SKIP LOCKED )
                 UPDATE events 
                 SET processing_at = now() 
                 FROM event_to_report
                 WHERE events.id = event_to_report.id
                 RETURNING 
                    event_to_report.data, 
                    event_to_report.external_id;
                """, new EventRowMapper()).stream().findFirst();
    }

    @Override
    public void setToProcessed(UUID id) {
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

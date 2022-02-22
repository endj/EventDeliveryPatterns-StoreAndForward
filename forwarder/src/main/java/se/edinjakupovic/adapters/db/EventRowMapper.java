package se.edinjakupovic.adapters.db;

import org.springframework.jdbc.core.RowMapper;
import se.edinjakupovic.core.Event;
import se.edinjakupovic.core.EventToSend;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;


public class EventRowMapper implements RowMapper<EventToSend> {
    @Override
    public EventToSend mapRow(ResultSet rs, int rowNum) throws SQLException {
        return EventToSend.builder()
                .event(Event
                        .builder()
                        .data(rs.getString("data"))
                        .id(UUID.fromString(rs.getString("external_id")))
                        .build())
                .attempts(rs.getInt("attempts"))
                .build();
    }
}

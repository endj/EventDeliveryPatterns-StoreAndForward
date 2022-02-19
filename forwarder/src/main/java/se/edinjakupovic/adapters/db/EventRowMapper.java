package se.edinjakupovic.adapters.db;

import org.springframework.jdbc.core.RowMapper;
import se.edinjakupovic.core.Event;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;


public class EventRowMapper implements RowMapper<Event> {
    @Override
    public Event mapRow(ResultSet rs, int rowNum) throws SQLException {
        return Event.builder()
                .data(rs.getString("data"))
                .id(UUID.fromString(rs.getString("external_id")))
                .build();
    }
}

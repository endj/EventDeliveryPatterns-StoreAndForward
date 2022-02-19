package se.edinjakupovic.core.ports;

import se.edinjakupovic.core.EventResponse;
import se.edinjakupovic.core.Event;

import java.util.Optional;
import java.util.UUID;

public interface EventRepository {

    EventResponse save(Event data);

    Optional<Event> getNextUnprocessed();

    void setToProcessed(UUID id);

    void setToFailed(UUID id);


}

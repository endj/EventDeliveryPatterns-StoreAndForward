package se.edinjakupovic.core.ports;

import se.edinjakupovic.core.Event;
import se.edinjakupovic.core.EventResponse;
import se.edinjakupovic.core.EventToSend;

import java.util.Optional;
import java.util.UUID;

public interface EventRepository {

    EventResponse save(Event data);

    Optional<EventToSend> getNextProcessable();

    void setToProcessed(UUID id);

    void setToFailed(UUID id);

}

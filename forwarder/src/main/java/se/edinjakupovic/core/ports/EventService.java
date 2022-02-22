package se.edinjakupovic.core.ports;

import se.edinjakupovic.core.Event;
import se.edinjakupovic.core.EventResponse;

public interface EventService {

    EventResponse create(Event data);
}


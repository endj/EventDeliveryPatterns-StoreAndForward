package se.edinjakupovic.core.ports;

import se.edinjakupovic.core.EventResponse;
import se.edinjakupovic.core.Event;

public interface EventService {

    EventResponse create(Event data);
}


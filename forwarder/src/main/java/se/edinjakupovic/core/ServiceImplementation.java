package se.edinjakupovic.core;

import se.edinjakupovic.core.ports.EventRepository;
import se.edinjakupovic.core.ports.EventService;

public class ServiceImplementation implements EventService {

    private final EventRepository eventRepository;

    public ServiceImplementation(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @Override
    public EventResponse create(Event data) {
        return eventRepository.save(data);
    }
}


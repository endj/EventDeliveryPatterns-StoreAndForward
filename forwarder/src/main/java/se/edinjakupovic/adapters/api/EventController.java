package se.edinjakupovic.adapters.api;

import se.edinjakupovic.core.EventResponse;
import se.edinjakupovic.core.ports.EventService;
import se.edinjakupovic.core.Event;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequestMapping("/forwarder/event")
@RestController
public class EventController  {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @PostMapping
    public EventResponse create(@RequestBody Event event) {
        log.info("Got request {}", event);
        return eventService.create(event);
    }
}


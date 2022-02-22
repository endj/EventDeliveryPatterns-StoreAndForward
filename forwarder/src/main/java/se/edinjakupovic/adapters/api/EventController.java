package se.edinjakupovic.adapters.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import se.edinjakupovic.core.Event;
import se.edinjakupovic.core.EventResponse;
import se.edinjakupovic.core.ports.EventService;

@Slf4j
@RequestMapping("/forwarder/event")
@RestController
public class EventController {

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


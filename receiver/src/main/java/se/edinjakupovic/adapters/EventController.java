package se.edinjakupovic.adapters;

import se.edinjakupovic.core.Event;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;


@Slf4j
@RequestMapping("/event-receiver/event")
@RestController
public class EventController  {

    @PostMapping
    public void create(@RequestBody Event event) {
        log.info("Received event {}", event);
    }
}


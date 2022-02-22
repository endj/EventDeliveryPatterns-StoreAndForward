package se.edinjakupovic.adapters.reporter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import se.edinjakupovic.core.ports.EventRepository;
import se.edinjakupovic.core.ports.EventSender;

@Slf4j
public class ScheduledEventReporter {

    private final EventSender eventReporter;
    private final EventRepository eventRepository;

    public ScheduledEventReporter(EventSender eventReporter, EventRepository eventRepository) {
        this.eventReporter = eventReporter;
        this.eventRepository = eventRepository;
    }

    @Scheduled(fixedDelay = 5000)
    public void sendNext() {
        eventRepository.getNextProcessable()
                .ifPresent(eventReporter::sendEvent);
    }
}

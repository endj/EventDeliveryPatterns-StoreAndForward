package se.edinjakupovic.adapters.reporter;

import lombok.extern.slf4j.Slf4j;
import se.edinjakupovic.core.EventReportResult;
import se.edinjakupovic.core.EventToSend;
import se.edinjakupovic.core.ports.EventRepository;
import se.edinjakupovic.core.ports.EventSender;

@Slf4j
public class Sender implements EventSender {
    private final ReceiverClient client;
    private final EventRepository repository;

    public Sender(ReceiverClient client, EventRepository repository) {
        this.client = client;
        this.repository = repository;
    }

    @Override
    public EventReportResult sendEvent(EventToSend event) {
        var eventData = event.getEvent();
        EventReportResult eventReportResult = client.sendEvent(eventData);
        if (eventReportResult.isSuccess()) {
            repository.setToProcessed(eventData.getId());
        } else {
            if (event.getAttempts() > 2)
                repository.setToFailed(eventData.getId());
        }
        return eventReportResult;
    }


}

package se.edinjakupovic.core.ports;

import se.edinjakupovic.core.EventReportResult;
import se.edinjakupovic.core.Event;

import java.util.UUID;

public interface EventReporter {

    EventReportResult forwardEvent(Event event);

    void setToProcessed(UUID id);

    void setToFailed(UUID id);
}

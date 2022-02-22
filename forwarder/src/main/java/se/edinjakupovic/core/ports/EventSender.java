package se.edinjakupovic.core.ports;

import se.edinjakupovic.core.EventReportResult;
import se.edinjakupovic.core.EventToSend;

public interface EventSender {

    EventReportResult sendEvent(EventToSend event);

}

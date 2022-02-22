package se.edinjakupovic.core;


import lombok.Builder;
import lombok.Value;

@Value
@Builder(toBuilder = true)
public class EventToSend {
    Event event;
    int attempts;
}

package se.edinjakupovic.core;

import lombok.Value;
import lombok.Builder;
import lombok.AllArgsConstructor;

import java.util.UUID;


@Value
@Builder(toBuilder = true)
@AllArgsConstructor
public class Event {
    String data;
    UUID id;
}
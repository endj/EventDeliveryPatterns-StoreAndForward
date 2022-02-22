package se.edinjakupovic.core;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

import java.util.UUID;


@Value
@Builder(toBuilder = true)
@AllArgsConstructor
public class Event {
    String data;
    UUID id;
}
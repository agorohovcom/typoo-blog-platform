package com.agorohov.shared.common.event;

import java.time.Instant;
import java.util.UUID;

public interface TypooEvent {
    UUID getEventId();
    Instant getOccurredAt();
    TypooEventType getEventType();
    String getSubjectId();
}

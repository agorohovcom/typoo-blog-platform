package com.agorohov.shared.common.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Getter
@RequiredArgsConstructor
public abstract class BaseTypooEvent implements TypooEvent {
    private final UUID eventId = UUID.randomUUID();
    private final Instant occurredAt = Instant.now();
    private final String subjectId;

    @Override
    public abstract TypooEventType getEventType();

}

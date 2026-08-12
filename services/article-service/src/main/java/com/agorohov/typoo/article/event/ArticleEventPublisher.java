package com.agorohov.typoo.article.event;

import com.agorohov.shared.common.event.TypooEvent;
import com.agorohov.shared.common.event.TypooEventPublisher;
import lombok.CustomLog;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@CustomLog
public class ArticleEventPublisher implements TypooEventPublisher {

    private final ApplicationEventPublisher publisher;

    @Override
    public void publish(TypooEvent event) {
        publisher.publishEvent(event);
        log.debug("Event {} was published", event.getEventType());
    }
}

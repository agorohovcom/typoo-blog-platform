package com.agorohov.typoo.article.event;

import com.agorohov.shared.common.event.TypooEvent;
import com.agorohov.shared.common.event.TypooEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ArticleEventPublisher implements TypooEventPublisher {

    private final ApplicationEventPublisher publisher;

    @Override
    public void publish(TypooEvent event) {
        publisher.publishEvent(event);
        log.debug("Event {} was published", event.getEventType());
    }
}

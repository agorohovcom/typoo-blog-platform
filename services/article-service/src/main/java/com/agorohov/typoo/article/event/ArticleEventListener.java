package com.agorohov.typoo.article.event;

import com.agorohov.typoo.article.service.ArticleService;
import lombok.CustomLog;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@CustomLog
public class ArticleEventListener {

    private final ArticleService articleService;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(ArticleRevisionCreatedEvent event) {
        log.debug("Handling event: {}", event.getEventType().getCode());

        UUID articleId = UUID.fromString(event.getSubjectId());
        articleService.cleanupOldestRevisions(articleId);
    }
}

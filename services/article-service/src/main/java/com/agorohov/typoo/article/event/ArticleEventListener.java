package com.agorohov.typoo.article.event;

import com.agorohov.typoo.article.service.ArticleRevisionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class ArticleEventListener {

    private final ArticleRevisionService articleRevisionService;

    // todo вынести в конфиг класс
    private static final int REVISION_MAX_COUNT = 10;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(ArticleRevisionCreatedEvent event) {
        log.debug("Handling event: {}", event.getEventType().getCode());

        UUID articleId = UUID.fromString(event.getSubjectId());
        int countOfRevisions = articleRevisionService.getCountRevisionsByArticleId(articleId);
        int toDelete = countOfRevisions - REVISION_MAX_COUNT;
        if (toDelete > 0) {
            int deleted = articleRevisionService.deleteOldestRevisions(articleId, toDelete);
            log.info("Deleted {} old revisions for article {}", deleted, articleId);
        }
    }
}

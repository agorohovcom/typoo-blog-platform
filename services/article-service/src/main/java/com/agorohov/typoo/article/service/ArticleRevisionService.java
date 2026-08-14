package com.agorohov.typoo.article.service;

import com.agorohov.shared.common.event.TypooEventPublisher;
import com.agorohov.typoo.article.entity.ArticleEntity;
import com.agorohov.typoo.article.entity.ArticleRevisionEntity;
import com.agorohov.typoo.article.event.ArticleRevisionCreatedEvent;
import com.agorohov.typoo.article.repository.ArticleRevisionRepository;
import lombok.CustomLog;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Limit;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@CustomLog
public class ArticleRevisionService {

    private final ArticleRevisionRepository articleRevisionRepository;
    private final TypooEventPublisher eventPublisher;

    // todo не принимать Entity из другого сервиса!!! Использовать ДТО
    @Transactional
    public void saveArticleRevision(ArticleEntity articleEntity, String revisionComment) {
        log.debug("Creating article revision for article id {} with revision comment [{}]",
                articleEntity.getId(), revisionComment);

        int revisionNumber = findLastRevisionNumberForArticle(articleEntity.getId()).orElse(0) + 1;

        ArticleRevisionEntity entity = new ArticleRevisionEntity();
        entity.setArticle(articleEntity);
        entity.setTitle(articleEntity.getTitle());
        entity.setSlug(articleEntity.getSlug());
        entity.setDescription(articleEntity.getDescription());
        entity.setContent(articleEntity.getContent());
        entity.setCoverImageId(articleEntity.getCoverImageId());
        entity.setCoverImageAlt(articleEntity.getCoverImageAlt());
        entity.setRevisionComment(revisionComment);
        entity.setRevisionNumber(revisionNumber);

        articleRevisionRepository.save(entity);
        log.info("Article revision number {} saved for article id {}", revisionNumber, articleEntity.getId());

        eventPublisher.publish(new ArticleRevisionCreatedEvent(articleEntity.getId()));
    }

    public int getCountRevisionsByArticleId(UUID articleId) {
        return articleRevisionRepository.getCountRevisionsByArticleId(articleId);
    }

    // Вариант 1: через один нативный запрос
    @Transactional
    public int deleteOldestRevisions(UUID articleId, int toDelete) {
        return articleRevisionRepository.deleteOldestRevisions(articleId, toDelete);
    }

    // Вариант 2: через 2 запроса без нативного
    @Transactional
    public int deleteOldestRevisionsV2(UUID articleId, int toDelete) {
        if (toDelete <= 0) {
            return 0;
        }

        // Шаг 1: Получаем список UUID самых старых ревизий (размером не более toDelete)
        List<UUID> idsToDelete = articleRevisionRepository.findOldestRevisionIds(
                articleId,
                Limit.of(toDelete)
        );

        // Если удалять нечего, сразу выходим
        if (idsToDelete.isEmpty()) {
            return 0;
        }

        // Шаг 2: Удаляем записи по зафиксированным ID одним запросом
        return articleRevisionRepository.deleteByIds(idsToDelete);
    }

    private Optional<Integer> findLastRevisionNumberForArticle(UUID articleId) {
        return articleRevisionRepository.findLastRevisionNumberForArticle(articleId);
    }
}

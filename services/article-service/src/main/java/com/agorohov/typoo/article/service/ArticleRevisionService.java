package com.agorohov.typoo.article.service;

import com.agorohov.typoo.article.entity.ArticleEntity;
import com.agorohov.typoo.article.entity.ArticleRevisionEntity;
import com.agorohov.typoo.article.repository.ArticleRevisionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ArticleRevisionService {

    private final ArticleRevisionRepository articleRevisionRepository;

    // todo вынести в конфиг класс
    private static final int REVISION_MAX_COUNT = 10;

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
        entity.setCreatedAt(Instant.now());         // нужно ли вручную?

        articleRevisionRepository.save(entity);
        log.info("Article revision number {} saved for article id {}", revisionNumber, articleEntity.getId());

        // todo реализовать асинхронный ивент, который проверит не превышено ли кол-во ревизий и если да - удалит лишние
    }

    private Optional<Integer> findLastRevisionNumberForArticle(UUID articleId) {
        return articleRevisionRepository.findLastRevisionNumberForArticle(articleId);
    }
}

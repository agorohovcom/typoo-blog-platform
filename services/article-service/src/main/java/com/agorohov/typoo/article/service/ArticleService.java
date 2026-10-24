package com.agorohov.typoo.article.service;

import com.agorohov.shared.common.event.TypooEventPublisher;
import com.agorohov.shared.common.exception.TypooException;
import com.agorohov.typoo.article.dto.ArticleItemResponse;
import com.agorohov.typoo.article.dto.ArticleResponse;
import com.agorohov.typoo.article.dto.ArticleStatusRequest;
import com.agorohov.typoo.article.dto.CreateArticleRequest;
import com.agorohov.typoo.article.dto.UpdateArticleRequest;
import com.agorohov.typoo.article.entity.ArticleEntity;
import com.agorohov.typoo.article.entity.ArticleRevisionEntity;
import com.agorohov.typoo.article.entity.ArticleSeoEntity;
import com.agorohov.typoo.article.entity.CategoryEntity;
import com.agorohov.typoo.article.entity.TagEntity;
import com.agorohov.typoo.article.event.ArticleRevisionCreatedEvent;
import com.agorohov.typoo.article.exception.ArticleErrorCode;
import com.agorohov.typoo.article.mapper.ArticleMapper;
import com.agorohov.typoo.article.repository.ArticleRepository;
import com.agorohov.typoo.article.repository.ArticleRevisionRepository;
import com.agorohov.typoo.article.repository.ArticleSeoRepository;
import com.agorohov.typoo.article.repository.CategoryRepository;
import com.agorohov.typoo.article.repository.TagRepository;
import com.agorohov.typoo.article.type.ArticleStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.CustomLog;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Limit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@CustomLog
@Validated
public class ArticleService {

    private final ArticleRepository articleRepository;
    private final CategoryRepository categoryRepository;
    private final TagRepository tagRepository;
    private final ArticleSeoRepository articleSeoRepository;
    private final ArticleRevisionRepository articleRevisionRepository;
    private final TypooEventPublisher eventPublisher;

    // todo вынести в конфиг класс
    private static final String CREATE_DRAFT_REVISION_COMMENT = "AUTO: First article draft created";
    private static final int REVISION_MAX_COUNT = 10;

    @Transactional
    public UUID createArticle(@NotNull @Valid CreateArticleRequest request) {
        log.debug(() -> "Creating article draft with title [{}]", request.getTitle());

        if (articleRepository.existsBySlug(request.getSlug())) {
            throw new TypooException(ArticleErrorCode.SLUG_ALREADY_EXISTS);
        }

        CategoryEntity category = null;
        if (request.getCategoryId() != null) {
            category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new TypooException(ArticleErrorCode.CATEGORY_NOT_FOUND));
        }

        Set<TagEntity> tags = Set.of();
        if (!CollectionUtils.isEmpty(request.getTagIds())) {
            tags = new HashSet<>(tagRepository.findAllById(request.getTagIds()));
        }

        ArticleEntity newArticle = new ArticleEntity();
        newArticle.setTitle(request.getTitle());
        newArticle.setSlug(request.getSlug());
        newArticle.setDescription(request.getDescription());
        newArticle.setContent(request.getContent());
        newArticle.setCategory(category);
        newArticle.setTags(tags);
        articleRepository.save(newArticle);

        createArticleRevision(newArticle, CREATE_DRAFT_REVISION_COMMENT);

        if (request.getMetaTitle() != null
                || request.getMetaDescription() != null
                || request.getMetaKeywords() != null) {
            ArticleSeoEntity seo = new ArticleSeoEntity();
            seo.setArticle(newArticle);
            seo.setMetaTitle(request.getMetaTitle());
            seo.setMetaDescription(request.getMetaDescription());
            seo.setMetaKeywords(request.getMetaKeywords());
            articleSeoRepository.save(seo);
        }

        log.info("Article draft with id {} created successfully", newArticle.getId());
        return newArticle.getId();
    }

    private void createArticleRevision(ArticleEntity articleEntity, String revisionComment) {
        log.debug(() -> "Creating article revision for article id {} with revision comment [{}]",
                articleEntity.getId(), revisionComment);

        long revisionNumber = articleEntity.getVersion() + 1;

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

    @Retryable(
            retryFor = ObjectOptimisticLockingFailureException.class,
            recover = "recoverUpdateArticle",
            maxAttempts = 3,
            backoff = @Backoff(delay = 50, multiplier = 2)
    )
    @Transactional
    public ArticleResponse updateArticle(@NotNull UUID id, @NotNull @Valid UpdateArticleRequest request) {
        log.debug(() -> "Updating article draft with id={}", id);

        ArticleEntity article = articleRepository.findById(id)
                .orElseThrow(() -> new TypooException(ArticleErrorCode.ARTICLE_NOT_FOUND));

        if (request.getSlug() != null && !request.getSlug().equals(article.getSlug())
                && articleRepository.existsBySlug(request.getSlug())) {
            throw new TypooException(ArticleErrorCode.SLUG_ALREADY_EXISTS);
        }

        ArticleMapper.updateArticleEntity(request, article);

        article.setUpdatedAt(Instant.now());

        // saveAndFlush гарантирует, что OptLockException бросится внутри метода (до createArticleRevision),
        // и что article.getVersion() в createArticleRevision уже новый - revisionNumber посчитается правильно.
        articleRepository.saveAndFlush(article);

        // fixme сейчас ревизия создается даже если ничего не поменялось
        createArticleRevision(article, request.getRevisionComment());

        return ArticleMapper.toArticleResponse(article);
    }

    @Recover
    public ArticleResponse recoverUpdateArticle(
            ObjectOptimisticLockingFailureException ex,
            UUID id,
            UpdateArticleRequest request
    ) {
        log.warn("Optimistic lock conflict on updating article {}", id);
        throw new TypooException(ArticleErrorCode.ARTICLE_CONCURRENTLY_MODIFIED);
    }

    // todo тоже нужен
    public ArticleResponse changeStatus(UUID id, ArticleStatusRequest request) {
        // todo При переходе в PUBLISHED нужно проставлять publishedAt
        //  При переходе из PUBLISHED в DRAFT — либо обнулять, либо оставлять (это продуктовое решение, нужно его зафиксировать).
        return null;
    }

    @Transactional
    public void archiveArticle(@NotNull UUID id) {
        log.debug(() -> "Archiving article with id {}", id);

        ArticleEntity article = articleRepository.findById(id)
                .orElseThrow(() -> new TypooException(ArticleErrorCode.ARTICLE_NOT_FOUND));

        article.setDeletedAt(Instant.now());
        article.setStatus(ArticleStatus.ARCHIVED);
        articleRepository.save(article);

        log.info("Article with id {} archived successfully", id);
    }

    @Transactional
    public void restoreArticle(@NotNull UUID id) {
        ArticleEntity article = articleRepository.findById(id)
                .orElseThrow(() -> new TypooException(ArticleErrorCode.ARTICLE_NOT_FOUND));

        if (!article.getStatus().equals(ArticleStatus.ARCHIVED)) {
            throw new TypooException(ArticleErrorCode.ARTICLE_NOT_ARCHIVED);
        }

        article.setDeletedAt(null);
        article.setStatus(ArticleStatus.DRAFT);
        articleRepository.save(article);
    }

    public Page<ArticleItemResponse> getPublishedArticleItems(
            Integer categoryId,
            @NotNull Pageable pageable
    ) {
        log.debug("Fetching published article items by category={}, page={}, size={}",
                categoryId, pageable.getPageNumber(), pageable.getPageSize());

        return articleRepository.findPublishedArticleItems(categoryId, pageable)
                .map(ArticleMapper::toArticleItemResponse);
    }

    public Page<ArticleItemResponse> getArticleItemsForAdmin(
            ArticleStatus status,
            Integer categoryId,
            String search,
            @NotNull Pageable pageable
    ) {
        log.debug("Fetching article items by status={}, category={}, search=[{}], page={}, size={}",
                status, categoryId, search, pageable.getPageNumber(), pageable.getPageSize());

        String notNullSearch = search == null ? "" : search.trim();
        return articleRepository.findArticleItemsForAdmin(status, categoryId, notNullSearch, pageable)
                .map(ArticleMapper::toArticleItemResponse);
    }

    public ArticleResponse getBySlug(@NotBlank String slug) {
        log.debug(() -> "Fetching article by slug: {}", slug);

        ArticleEntity article = articleRepository.findFullBySlug(slug)
                .orElseThrow(() -> new TypooException(ArticleErrorCode.ARTICLE_NOT_FOUND));

        return ArticleMapper.toArticleResponse(article);
    }

// ============================== Revision ===================================

    @Transactional
    public void cleanupOldestRevisions(UUID articleId) {
        int count = articleRevisionRepository.getCountRevisionsByArticleId(articleId);
        int toDelete = count - REVISION_MAX_COUNT;
        if (toDelete <= 0) {
            return;
        }

        List<UUID> ids = articleRevisionRepository.findOldestRevisionIds(articleId, Limit.of(toDelete));
        if (ids.isEmpty()) {
            return;
        }

        int deleted = articleRevisionRepository.deleteByIds(ids);
        log.info("Deleted {} old revisions for article {}", deleted, articleId);
    }
}

package com.agorohov.typoo.article.service;

import com.agorohov.shared.common.exception.TypooException;
import com.agorohov.typoo.article.dto.ArticleListItem;
import com.agorohov.typoo.article.dto.ArticleResponse;
import com.agorohov.typoo.article.dto.ArticleStatusRequest;
import com.agorohov.typoo.article.dto.CreateArticleRequest;
import com.agorohov.typoo.article.dto.UpdateArticleRequest;
import com.agorohov.typoo.article.entity.ArticleEntity;
import com.agorohov.typoo.article.entity.ArticleSeoEntity;
import com.agorohov.typoo.article.entity.CategoryEntity;
import com.agorohov.typoo.article.entity.TagEntity;
import com.agorohov.typoo.article.exception.ArticleErrorCode;
import com.agorohov.typoo.article.repository.ArticleRepository;
import com.agorohov.typoo.article.repository.ArticleSeoRepository;
import com.agorohov.typoo.article.repository.CategoryRepository;
import com.agorohov.typoo.article.repository.TagRepository;
import com.agorohov.typoo.article.type.ArticleStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ArticleService {

    private final ArticleRevisionService articleRevisionService;
    private final ArticleRepository articleRepository;
    private final CategoryRepository categoryRepository;
    private final TagRepository tagRepository;
    private final ArticleSeoRepository articleSeoRepository;

    // todo вынести в конфиг класс
    private static final String CREATE_DRAFT_REVISION_COMMENT = "AUTO: First article draft created";

    @Transactional
    public UUID createArticle(CreateArticleRequest request) {
        log.debug("Creating article draft with title [{}]", request.getTitle());

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
        newArticle.setStatus(ArticleStatus.DRAFT);
        newArticle.setCreatedAt(Instant.now());     // нужно ли вручную?
        newArticle.setCategory(category);
        newArticle.setTags(tags);
        articleRepository.save(newArticle);

        articleRevisionService.saveArticleRevision(newArticle, CREATE_DRAFT_REVISION_COMMENT);

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

    public ArticleResponse updateArticle(UUID id, UpdateArticleRequest request) {
        return null;
    }

    public ArticleResponse changeStatus(UUID id, ArticleStatusRequest request) {
        return null;
    }

    public void deleteArticle(UUID id) {

    }

    public Page<ArticleListItem> getArticles(Pageable pageable, ArticleStatus status, String categorySlug, String search) {
        return null;
    }

    public ArticleResponse getBySlug(String slug) {
        return null;
    }
}

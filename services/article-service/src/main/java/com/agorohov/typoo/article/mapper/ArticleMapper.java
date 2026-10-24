package com.agorohov.typoo.article.mapper;

import com.agorohov.typoo.article.dto.ArticleItemResponse;
import com.agorohov.typoo.article.dto.ArticleResponse;
import com.agorohov.typoo.article.dto.UpdateArticleRequest;
import com.agorohov.typoo.article.entity.ArticleEntity;
import com.agorohov.typoo.article.entity.TagEntity;
import com.agorohov.typoo.article.repository.projection.ArticleItemProjection;

import java.util.Objects;
import java.util.stream.Collectors;

// TODO MapStruct не используется
public class ArticleMapper {

    public static ArticleResponse toArticleResponse(ArticleEntity entity) {
        return ArticleResponse.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .slug(entity.getSlug())
                .description(entity.getDescription())
                .content(entity.getContent())
                .status(entity.getStatus())
                .coverImageId(entity.getCoverImageId())
                .coverImageAlt(entity.getCoverImageAlt())
                .categoryId(entity.getCategory() != null ? entity.getCategory().getId() : null)
                .categoryName(entity.getCategory() != null ? entity.getCategory().getName() : null)
                .tags(entity.getTags().stream().filter(Objects::nonNull).map(TagEntity::getName).collect(Collectors.toSet()))
                .allowComments(entity.getAllowComments())
                .featured(entity.getFeatured())
                .isPinned(entity.getIsPinned())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .publishedAt(entity.getPublishedAt())
                .build();
    }

    public static ArticleItemResponse toArticleItemResponse(ArticleItemProjection projection) {
        return ArticleItemResponse.builder()
                .id(projection.getId())
                .title(projection.getTitle())
                .description(projection.getDescription())
                .slug(projection.getSlug())
                .status(projection.getStatus())
                .coverImageId(projection.getCoverImageId())
                .coverImageAlt(projection.getCoverImageAlt())
                .publishedAt(projection.getPublishedAt())
                .categoryId(projection.getCategoryId())
                .categoryName(projection.getCategoryName())
                .build();
    }

    public static void updateArticleEntity(UpdateArticleRequest request, ArticleEntity entity) {
        if (request.getTitle() != null) {
            entity.setTitle(request.getTitle());
        }
        if (request.getSlug() != null) {
            entity.setSlug(request.getSlug());
        }
        if (request.getDescription() != null) {
            entity.setDescription(request.getDescription());
        }
        if (request.getContent() != null) {
            entity.setContent(request.getContent());
        }
        if (request.getCoverImageId() != null) {
            entity.setCoverImageId(request.getCoverImageId());
        }
        if (request.getCoverImageAlt() != null) {
            entity.setCoverImageAlt(request.getCoverImageAlt());
        }
    }
}

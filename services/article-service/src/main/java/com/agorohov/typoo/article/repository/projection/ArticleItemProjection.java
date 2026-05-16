package com.agorohov.typoo.article.repository.projection;

import com.agorohov.typoo.article.type.ArticleStatus;

import java.time.Instant;
import java.util.UUID;

public interface ArticleItemProjection {
    UUID getId();

    String getTitle();

    String getDescription();

    String getSlug();

    ArticleStatus getStatus();

    UUID getCoverImageId();

    String getCoverImageAlt();

    Instant getPublishedAt();

    Integer getCategoryId();

    String getCategoryName();
}

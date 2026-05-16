package com.agorohov.typoo.article.dto;

import com.agorohov.typoo.article.type.ArticleStatus;
import lombok.Builder;
import lombok.Value;

import java.time.Instant;
import java.util.UUID;

@Value
@Builder
public class ArticleItemResponse {

    UUID id;
    String title;
    String description;
    String slug;
    ArticleStatus status;
    UUID coverImageId;
    String coverImageAlt;
    Instant publishedAt;
    Integer categoryId;
    String categoryName;
}

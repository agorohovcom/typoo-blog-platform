package com.agorohov.typoo.article.dto;

import com.agorohov.typoo.article.type.ArticleStatus;
import lombok.Builder;
import lombok.Value;

import java.time.Instant;
import java.util.UUID;

@Value
@Builder
public class ArticleListItem {

    UUID id;
    String title;
    String slug;
    String description;
    ArticleStatus status;
    Instant publishedAt;
    String coverImageUrl;     // будет заполняться позже через Content Service
    UUID categoryId;
    String categoryName;
}

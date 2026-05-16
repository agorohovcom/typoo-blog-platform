package com.agorohov.typoo.article.dto;

import com.agorohov.typoo.article.type.ArticleStatus;
import lombok.Builder;
import lombok.Value;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

@Value
@Builder
public class ArticleResponse {

    UUID id;
    String title;
    String slug;
    String description;
    String content;             // fixme Надо ли возвращать статью при запросах?
    ArticleStatus status;

    UUID coverImageId;
    String coverImageAlt;

    Integer categoryId;
    String categoryName;

    Set<String> tags;            // todo Set<TagDto>

    Boolean allowComments;
    Boolean featured;
    Boolean isPinned;

    Instant createdAt;
    Instant updatedAt;
    Instant publishedAt;
}

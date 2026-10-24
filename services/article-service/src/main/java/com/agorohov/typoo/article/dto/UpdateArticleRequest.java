package com.agorohov.typoo.article.dto;

import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Value;

import java.util.UUID;

@Value
@Builder
public class UpdateArticleRequest {

    @Size(min = 3, max = 300)
    String title;

    String slug;                // читаемый урл

    @Size(max = 1000)
    String description;

    String content;

    UUID coverImageId;
    String coverImageAlt;

    String revisionComment;
}

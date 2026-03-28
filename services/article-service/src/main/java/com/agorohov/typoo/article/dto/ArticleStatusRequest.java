package com.agorohov.typoo.article.dto;

import com.agorohov.typoo.article.type.ArticleStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ArticleStatusRequest {

    @NotNull
    ArticleStatus status;

    String revisionComment;
}

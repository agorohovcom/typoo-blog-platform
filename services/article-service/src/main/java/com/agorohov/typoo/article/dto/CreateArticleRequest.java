package com.agorohov.typoo.article.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Value;

import java.util.Set;

@Value
@Builder
public class CreateArticleRequest {

    @NotBlank
    @Size(min = 3, max = 300)
    String title;

    @NotBlank
    String slug;                // читаемый урл, можно будет сделать функцию для авто транслитерации через Map

    @Size(max = 1000)
    String description;

    @NotBlank
    String content;

    Integer categoryId;
    Set<Integer> tagIds = Set.of();

    String metaTitle;
    String metaDescription;
    String metaKeywords;
}

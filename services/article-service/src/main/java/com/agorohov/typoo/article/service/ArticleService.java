package com.agorohov.typoo.article.service;

import com.agorohov.typoo.article.dto.CreateArticleDto;
import com.agorohov.typoo.article.repository.ArticleRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Validated
public class ArticleService {

    private final ArticleRepository articleRepository;

    public UUID createArticle(@NotNull @Valid CreateArticleDto request) {
        return null;
    }
}

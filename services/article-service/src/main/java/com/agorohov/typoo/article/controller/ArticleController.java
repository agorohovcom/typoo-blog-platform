package com.agorohov.typoo.article.controller;

import com.agorohov.typoo.article.dto.CreateArticleDto;
import com.agorohov.typoo.article.service.ArticleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ArticleController {

    private final ArticleService articleService;

    @PostMapping
    public ResponseEntity<UUID> createArticle(@RequestBody CreateArticleDto request) {
        log.debug("POST /article: {}", request);
        return ResponseEntity.ok(articleService.createArticle(request));
    }
}

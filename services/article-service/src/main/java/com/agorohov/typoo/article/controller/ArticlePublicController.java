package com.agorohov.typoo.article.controller;

import com.agorohov.typoo.article.dto.ArticleListItem;
import com.agorohov.typoo.article.dto.ArticleResponse;
import com.agorohov.typoo.article.service.ArticleService;
import com.agorohov.typoo.article.type.ArticleStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1")
public class ArticlePublicController {

    private final ArticleService articleService;

    @GetMapping
    public ResponseEntity<Page<ArticleListItem>> getArticles(
            @PageableDefault(size = 10) Pageable pageable,
            @RequestParam(required = false) ArticleStatus status,
            @RequestParam(required = false) String categorySlug,
            @RequestParam(required = false) String search
    ) {
        var page = articleService.getArticles(pageable, status, categorySlug, search);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/{slug}")
    public ResponseEntity<ArticleResponse> getBySlug(@PathVariable String slug) {
        var article = articleService.getBySlug(slug);
        return ResponseEntity.ok(article);
    }
}

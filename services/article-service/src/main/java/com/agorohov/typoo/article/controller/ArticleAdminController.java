package com.agorohov.typoo.article.controller;

import com.agorohov.typoo.article.dto.ArticleItemResponse;
import com.agorohov.typoo.article.dto.ArticleResponse;
import com.agorohov.typoo.article.dto.ArticleStatusRequest;
import com.agorohov.typoo.article.dto.CreateArticleRequest;
import com.agorohov.typoo.article.dto.UpdateArticleRequest;
import com.agorohov.typoo.article.service.ArticleService;
import com.agorohov.typoo.article.type.ArticleStatus;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/v1/admin")
public class ArticleAdminController {

    private final ArticleService articleService;

    @PostMapping
    public ResponseEntity<UUID> createDraft(@RequestBody @Valid CreateArticleRequest request) {
        var articleId = articleService.createArticle(request);
        return ResponseEntity.ok(articleId);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ArticleResponse> update(
            @PathVariable UUID id,
            @RequestBody @Valid UpdateArticleRequest request
    ) {
        var response = articleService.updateArticle(id, request);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ArticleResponse> changeStatus(
            @PathVariable UUID id,
            @RequestBody @Valid ArticleStatusRequest request
    ) {
        var response = articleService.changeStatus(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        articleService.deleteArticle(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<Page<ArticleItemResponse>> getArticleItems(
            @RequestParam(required = false) ArticleStatus status,
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(required = false) String search,
            @PageableDefault(size = 10) Pageable pageable
    ) {
        var page = articleService.getArticleItemsForAdmin(status, categoryId, search, pageable);
        return ResponseEntity.ok(page);
    }
}

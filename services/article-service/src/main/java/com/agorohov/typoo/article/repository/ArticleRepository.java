package com.agorohov.typoo.article.repository;

import com.agorohov.typoo.article.entity.ArticleEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ArticleRepository extends JpaRepository<ArticleEntity, UUID> {

    // ============================== Для частичного ответа ===============================

    // TODO реализовать projection, например:

    // ====================== Для полной статьи (детальная страница) ======================

    @EntityGraph(attributePaths = {"category", "tags", "seo"})
    @Query("SELECT a FROM ArticleEntity a WHERE a.slug = :slug AND a.deletedAt IS NULL")
    Optional<ArticleEntity> findFullBySlug(@Param("slug") String slug);

    @EntityGraph(attributePaths = {"category", "tags", "seo"})
    @Query("SELECT a FROM ArticleEntity a WHERE a.id = :id AND a.deletedAt IS NULL")
    Optional<ArticleEntity> findFullById(@Param("id") UUID id);
}

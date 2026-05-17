package com.agorohov.typoo.article.repository;

import com.agorohov.typoo.article.entity.ArticleEntity;
import com.agorohov.typoo.article.repository.projection.ArticleItemProjection;
import com.agorohov.typoo.article.type.ArticleStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ArticleRepository extends JpaRepository<ArticleEntity, UUID> {

    @Query("""
            SELECT
                a.id AS id,
                a.title AS title,
                a.description AS description,
                a.slug AS slug,
                a.status AS status,
                a.coverImageId AS coverImageId,
                a.coverImageAlt AS coverImageAlt,
                a.publishedAt AS publishedAt,
                c.id AS categoryId,
                c.name AS categoryName
            FROM ArticleEntity a
            LEFT JOIN a.category c
            WHERE a.status = 'PUBLISHED'
                AND a.deletedAt IS NULL
                AND (:categoryId IS NULL OR a.category.id = :categoryId)
                AND (:search = ''
                    OR LOWER(a.title) LIKE CONCAT('%', LOWER(:search), '%')
                    OR LOWER(a.description) LIKE CONCAT('%', LOWER(:search), '%'))
            ORDER BY
                a.publishedAt DESC,
                a.createdAt DESC
            """)
    Page<ArticleItemProjection> findPublishedArticleItems(
            @Param("categoryId") Integer categoryId,
            @Param("search") String search,
            Pageable pageable
    );

    // todo для публичных ручек выдавать только в статусе PUBLISHED
    @EntityGraph(attributePaths = {"category", "tags", "seo"})
    @Query("SELECT a FROM ArticleEntity a WHERE a.slug = :slug AND a.deletedAt IS NULL")
    Optional<ArticleEntity> findFullBySlug(@Param("slug") String slug);

    // todo для публичных ручек выдавать только в статусе PUBLISHED
    @EntityGraph(attributePaths = {"category", "tags", "seo"})
    @Query("SELECT a FROM ArticleEntity a WHERE a.id = :id AND a.deletedAt IS NULL")
    Optional<ArticleEntity> findFullById(@Param("id") UUID id);

    // ============================== ADMIN METHODS ====================================

    @Query("""
            SELECT
                a.id AS id,
                a.title AS title,
                a.description AS description,
                a.slug AS slug,
                a.status AS status,
                a.coverImageId AS coverImageId,
                a.coverImageAlt AS coverImageAlt,
                a.publishedAt AS publishedAt,
                c.id AS categoryId,
                c.name AS categoryName
            FROM ArticleEntity a
            LEFT JOIN a.category c
            WHERE (:status IS NULL OR a.status = :status)
                AND (:categoryId IS NULL OR a.category.id = :categoryId)
                AND (:search = ''
                    OR LOWER(a.title) LIKE CONCAT('%', LOWER(:search), '%')
                    OR LOWER(a.description) LIKE CONCAT('%', LOWER(:search), '%'))
            ORDER BY
                COALESCE(a.publishedAt, a.createdAt) DESC,
                a.createdAt DESC
            """)
    Page<ArticleItemProjection> findArticleItemsForAdmin(
            @Param("status") ArticleStatus status,
            @Param("categoryId") Integer categoryId,
            @Param("search") String search,
            Pageable pageable
    );
}

package com.agorohov.typoo.article.repository;

import com.agorohov.typoo.article.entity.ArticleRevisionEntity;
import org.springframework.data.domain.Limit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ArticleRevisionRepository extends JpaRepository<ArticleRevisionEntity, UUID> {

    @Query("""
            SELECT ar.revisionNumber FROM ArticleRevisionEntity ar
            WHERE ar.article.id = :articleId
            ORDER BY ar.createdAt DESC, ar.revisionNumber DESC
            LIMIT 1""")
    Optional<Integer> findLastRevisionNumberForArticle(@Param("articleId") UUID articleId);

    @Query("""
            SELECT COUNT(ar) FROM ArticleRevisionEntity ar
            WHERE ar.article.id = :articleId""")
    int getCountRevisionsByArticleId(@Param("articleId") UUID articleId);

    // Кажется, в JPQL нет LIMIT, поэтому надо либо использовать такого типа нативный запрос,
    // либо через 2 запроса в сервисе (реализовано ниже)
    // Вариант 1: через нативный запрос
    @Modifying(clearAutomatically = true)
    @Query(value = """
                DELETE FROM article_revision ar1
                WHERE ar1.article_id = :articleId
                AND id IN (
                    SELECT ar2.id FROM article_revision ar2
                    WHERE ar2.article_id = :articleId
                    ORDER BY ar2.created_at ASC, ar2.revision_number ASC
                    LIMIT :deleteCount
            )""", nativeQuery = true)
    // Версия JPQL с LIMIT вроде как не сработает, но я не проверял
//    @Query("""
//            DELETE FROM ArticleRevisionEntity ar
//            WHERE ar.article.id = :articleId
//            AND ar.id IN (
//                SELECT ar2.id FROM ArticleRevisionEntity ar2
//                WHERE ar2.article.id = :articleId
//                ORDER BY ar2.createdAt ASC, ar2.revisionNumber ASC
//                LIMIT :deleteCount
//            )""")
    int deleteOldestRevisions(@Param("articleId") UUID articleId, @Param("deleteCount") int deleteCount);

    // Вариант 2: через 2 запроса без нативного.
    // Используем Query Method. Spring Data сам сгенерирует правильный JPQL.
    // Выбираем только ID, чтобы не тянуть тяжелый контент (текст статьи) по сети
    @Query("""
            SELECT ar.id FROM ArticleRevisionEntity ar
            WHERE ar.article.id = :articleId
            ORDER BY ar.createdAt ASC, ar.revisionNumber ASC
            """)
    List<UUID> findOldestRevisionIds(@Param("articleId") UUID articleId, Limit limit);

    // Метод массового удаления по списку ID
    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM ArticleRevisionEntity ar WHERE ar.id IN :ids")
    int deleteByIds(@Param("ids") List<UUID> ids);
}

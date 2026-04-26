package com.agorohov.typoo.article.repository;

import com.agorohov.typoo.article.entity.ArticleRevisionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ArticleRevisionRepository extends JpaRepository<ArticleRevisionEntity, UUID> {

    @Query("""
            SELECT ar.revisionNumber FROM ArticleRevisionEntity ar \
            WHERE ar.article.id = :articleId \
            ORDER BY ar.createdAt DESC, ar.revisionNumber DESC \
            LIMIT 1""")
    Optional<Integer> findLastRevisionNumberForArticle(@Param("articleId") UUID articleId);

    @Query("""
            SELECT COUNT(ar) FROM ArticleRevisionEntity ar \
            WHERE ar.article.id = :articleId""")
    int getCountRevisionsByArticleId(@Param("articleId") UUID articleId);

    @Modifying
    @Query("""
            DELETE FROM ArticleRevisionEntity ar \
            WHERE ar.article.id = :articleId \
            AND ar.id IN (\
                SELECT ar2.id FROM ArticleRevisionEntity ar2 \
                WHERE ar2.article.id = :articleId \
                ORDER BY ar2.createdAt ASC, ar2.revisionNumber ASC \
                LIMIT :deleteCount\
            )""")
    int deleteOldestRevisions(@Param("articleId") UUID articleId, @Param("deleteCount") int deleteCount);
}

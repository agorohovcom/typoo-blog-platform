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

    // todo удалить? теперь номер ревизии в версии статьи
    @Query("""
            SELECT MAX(ar.revisionNumber)
            FROM ArticleRevisionEntity ar
            WHERE ar.article.id = :articleId
            """)
    Optional<Integer> findLastRevisionNumberForArticle(@Param("articleId") UUID articleId);

    @Query("""
            SELECT COUNT(ar) FROM ArticleRevisionEntity ar
            WHERE ar.article.id = :articleId""")
    int getCountRevisionsByArticleId(@Param("articleId") UUID articleId);

    @Query("""
            SELECT ar.id FROM ArticleRevisionEntity ar
            WHERE ar.article.id = :articleId
            ORDER BY ar.createdAt ASC, ar.revisionNumber ASC
            """)
    List<UUID> findOldestRevisionIds(@Param("articleId") UUID articleId, Limit limit);

    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM ArticleRevisionEntity ar WHERE ar.id IN :ids")
    int deleteByIds(@Param("ids") List<UUID> ids);
}

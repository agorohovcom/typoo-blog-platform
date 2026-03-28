package com.agorohov.typoo.article.repository;

import com.agorohov.typoo.article.entity.ArticleSeoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ArticleSeoRepository extends JpaRepository<ArticleSeoEntity, UUID> {
}

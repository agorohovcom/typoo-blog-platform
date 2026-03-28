package com.agorohov.typoo.article.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;

@Entity
@Table(name = "article_seo")
@Getter
@Setter
@ToString(exclude = "article")
@NoArgsConstructor
@AllArgsConstructor
public class ArticleSeoEntity {

    @Id
    @Column(name = "article_id")
    private UUID id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "article_id")
    private ArticleEntity article;

    @Column(name = "meta_title", length = 500)
    private String metaTitle;               // <title>Мета-заголовок</title>

    @Column(name = "meta_description", length = 1000)
    private String metaDescription;         // <meta name="description" content="...">

    @Column(name = "meta_keywords", length = 500)
    private String metaKeywords;            // <meta name="keywords" content="блог, горохов, java, backend">

    @Column(name = "canonical_url", length = 1000)
    private String canonicalUrl;            // <link rel="canonical" href="https://agorohov.com/правильный-url">
}

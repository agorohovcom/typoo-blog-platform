package com.agorohov.typoo.article.entity;

import com.agorohov.typoo.article.type.ArticleStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "articles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode(of = "id")
public class ArticleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "title", nullable = false, length = 300)
    private String title;

    @Column(name = "slug", nullable = false, unique = true, length = 300)
    private String slug;                    // читаемый урл

    @Column(name = "description", length = 1000)
    private String description;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ArticleStatus status;

    // Associations
    @ManyToOne
    @JoinColumn(name = "category_id")
    private CategoryEntity category;

    @ManyToMany
    @JoinTable(
            name = "article_tags",
            joinColumns = @JoinColumn(name = "article_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Set<TagEntity> tags = new HashSet<>();

    // Timestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at")
    private Instant updatedAt;

    @Column(name = "published_at")
    private Instant publishedAt;

    @Column(name = "deleted_at")
    private Instant deletedAt;

    // Flags
    @Column(name = "allow_comments", nullable = false)
    private Boolean allowComments = true;

    @Column(name = "featured", nullable = false)
    private Boolean featured = false;

    @Column(name = "is_pinned", nullable = false)
    private Boolean isPinned = false;

    // SEO
    @Column(name = "meta_title", length = 500)
    private String metaTitle;               // <title>Мета-заголовок</title>

    @Column(name = "meta_description", length = 1000)
    private String metaDescription;         // <meta name="description" content="...">

    @Column(name = "meta_keywords")
    private String metaKeywords;            // <meta name="keywords" content="блог, горохов, java, backend">

    @Column(name = "canonical_url", length = 1000)
    private String canonicalUrl;            // <link rel="canonical" href="https://agorohov.com/правильный-url">
}

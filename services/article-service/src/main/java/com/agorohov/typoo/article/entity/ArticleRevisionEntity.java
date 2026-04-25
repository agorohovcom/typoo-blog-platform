package com.agorohov.typoo.article.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "article_revision")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode(of = "id")
public class ArticleRevisionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "article_id", nullable = false)
    private ArticleEntity article;

    @Column(name = "title", nullable = false, length = 300, updatable = false)
    private String title;

    @Column(name = "slug", nullable = false, length = 300, updatable = false)
    private String slug;

    @Column(name = "description", length = 1000, updatable = false)
    private String description;

    @Column(name = "content", updatable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "cover_image_id", updatable = false)
    private UUID coverImageId;

    @Column(name = "cover_image_alt", length = 500, updatable = false)
    private String coverImageAlt;

    @Column(name = "revision_comment", length = 1000)
    private String revisionComment;

    @Column(name = "revision_number", nullable = false, updatable = false)
    private Integer revisionNumber;             // todo при переходе на миграции сделать уник(артикль, номерРевизии)

    @Column(name = "created_at", updatable = false)
    private Instant createdAt = Instant.now();
}

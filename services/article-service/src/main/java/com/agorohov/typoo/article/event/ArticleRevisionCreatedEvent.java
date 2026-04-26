package com.agorohov.typoo.article.event;

import com.agorohov.shared.common.event.BaseTypooEvent;
import com.agorohov.shared.common.event.TypooEventType;
import lombok.Getter;

import java.util.UUID;

import static com.agorohov.typoo.article.event.ArticleEventType.ARTICLE_REVISION_CREATED;

@Getter
public class ArticleRevisionCreatedEvent extends BaseTypooEvent {

    public ArticleRevisionCreatedEvent(UUID articleId) {
        super(articleId.toString());
    }

    @Override
    public TypooEventType getEventType() {
        return ARTICLE_REVISION_CREATED;
    }
}

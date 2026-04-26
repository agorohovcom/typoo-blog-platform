package com.agorohov.typoo.article.event;

import com.agorohov.shared.common.event.TypooEventType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ArticleEventType implements TypooEventType {

    ARTICLE_REVISION_CREATED("ARTICLE_REVISION_CREATED", "New article revision created");

    private final String code;
    private final String description;

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getDescription() {
        return description;
    }
}

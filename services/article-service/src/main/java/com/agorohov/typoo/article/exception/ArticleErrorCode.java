package com.agorohov.typoo.article.exception;

import com.agorohov.shared.common.exception.ErrorCode;
import org.springframework.http.HttpStatus;

public enum ArticleErrorCode implements ErrorCode {

    UNEXPECTED_INTERNAL_ERROR("UNEXPECTED_INTERNAL_ERROR", "Handled unexpected internal error", HttpStatus.INTERNAL_SERVER_ERROR.value()),
    ARTICLE_NOT_FOUND("ARTICLE_NOT_FOUND", "Article not found", HttpStatus.NOT_FOUND.value()),
    SLUG_ALREADY_EXISTS("SLUG_ALREADY_EXISTS", "Article with the some slug already exists", HttpStatus.CONFLICT.value()),
    CATEGORY_NOT_FOUND("CATEGORY_NOT_FOUND", "Category not found", HttpStatus.NOT_FOUND.value());

    private final String code;
    private final String message;
    private final int httpStatusCode;

    ArticleErrorCode(String code, String message, int httpStatusCode) {
        this.code = code;
        this.message = message;
        this.httpStatusCode = httpStatusCode;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public int getHttpStatusCode() {
        return httpStatusCode;
    }
}

package com.agorohov.typoo.article.exception;

import com.agorohov.shared.common.exception.ErrorCode;
import com.agorohov.shared.common.exception.TypooException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.Instant;
import java.util.Map;

@ControllerAdvice
public class ArticleExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(TypooException.class)
    public ResponseEntity<Object> handleTypooException(TypooException ex) {
        ErrorCode code = ex.getErrorCode();

        var body = Map.of(
                "timestamp", Instant.now(),
                "error", code.getCode(),
                "message", code.getMessage(),
                "details", ex.getDetails() != null ? ex.getDetails() : "",
                "status", code.getHttpStatusCode()
        );

        return ResponseEntity
                .status(code.getHttpStatusCode())
                .body(body);
    }
}

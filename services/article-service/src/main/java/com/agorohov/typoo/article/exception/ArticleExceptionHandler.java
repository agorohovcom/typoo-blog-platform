package com.agorohov.typoo.article.exception;

import com.agorohov.shared.common.exception.ErrorCode;
import com.agorohov.shared.common.exception.TypooException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.Instant;
import java.util.Map;

import static com.agorohov.typoo.article.exception.ArticleErrorCode.UNEXPECTED_INTERNAL_ERROR;

@ControllerAdvice
@Slf4j
public class ArticleExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleUnexpectedException(Exception ex) {
        log.error("Unexpected error occurred, type: {}, message: {}", ex.getClass().getSimpleName(), ex.getMessage());
        String details = "Error type: " + ex.getClass().getSimpleName();
        return buildResponseEntity(UNEXPECTED_INTERNAL_ERROR, details);
    }

    @ExceptionHandler(TypooException.class)
    public ResponseEntity<Object> handleTypooException(TypooException ex) {
        ErrorCode code = ex.getErrorCode();
        return buildResponseEntity(code, ex.getDetails());
    }

    private ResponseEntity<Object> buildResponseEntity(ErrorCode code, String details) {
        var body = Map.of(
                "timestamp", Instant.now(),
                "code", code.getCode(),
                "message", code.getMessage(),
                "details", details != null ? details : "",
                "status", code.getHttpStatusCode()
        );

        return ResponseEntity
                .status(code.getHttpStatusCode())
                .body(body);
    }
}

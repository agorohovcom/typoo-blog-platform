package com.agorohov.typoo.article.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.StringJoiner;

@Slf4j
@Component
@Order(1)
public class RequestLoggingFilter extends OncePerRequestFilter {

    private static final int MAX_PAYLOAD_LENGTH = 8192;
    private static final int CONTENT_CACHE_LIMIT = 1024 * 64;
    private static final String[] SHOULD_NOT_FILTER = {
            "/actuator",
            "/health"
    };
    private static final String[] HEADERS_SHOULD_MASKED = {
            "Authorization",
            "Cookie"
    };
    private static final String MASKED = "[MASKED]";
    private static final String EMPTY_BODY = "[empty body]";

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request, CONTENT_CACHE_LIMIT);
        ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(response);

        long startTime = System.currentTimeMillis();

        try {
            filterChain.doFilter(wrappedRequest, wrappedResponse);
        } finally {
            logRequest(wrappedRequest);
            logResponse(wrappedResponse, startTime);
            wrappedResponse.copyBodyToResponse();
        }
    }

    private void logRequest(ContentCachingRequestWrapper request) {
        String method = request.getMethod();
        String uri = request.getRequestURI();
        String query = request.getQueryString() != null ? "?" + request.getQueryString() : "";

        String headers = getHeadersMasked(request);

        byte[] buf = request.getContentAsByteArray();

        String displayBody = EMPTY_BODY;

        if (buf.length > 0) {
            String contentType = request.getContentType();
            boolean shouldLogBody = contentType != null &&
                    contentType.contains(MediaType.APPLICATION_JSON_VALUE);

            if (shouldLogBody) {
                int len = Math.min(buf.length, MAX_PAYLOAD_LENGTH);
                displayBody = new String(buf, 0, len, StandardCharsets.UTF_8);
                if (buf.length > MAX_PAYLOAD_LENGTH) displayBody += " [truncated]";
            } else {
                displayBody = "[binary or large content skipped]";
            }
        }

        log.info("""
                        -> Request
                        method  : {}
                        uri     : {}{}
                        headers : {}
                        body    : {}
                        """,
                method,
                uri, query,
                headers,
                displayBody
        );
    }

    private void logResponse(ContentCachingResponseWrapper response, long startTime) {
        long duration = System.currentTimeMillis() - startTime;
        log.info("""
                        <- Response
                        status  : {}
                        time    : {} ms
                        """,
                response.getStatus(),
                duration
        );
    }

    private String getHeadersMasked(ContentCachingRequestWrapper request) {
        StringJoiner headersJoiner = new StringJoiner(", ");
        Collections.list(request.getHeaderNames()).forEach(name -> {
            String value = Arrays.stream(HEADERS_SHOULD_MASKED).anyMatch(name::equalsIgnoreCase)
                    ? MASKED
                    : request.getHeader(name);
            headersJoiner.add(name + ": " + value);
        });
        return headersJoiner.toString();
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return Arrays.stream(SHOULD_NOT_FILTER)
                .anyMatch(path::startsWith);
    }
}

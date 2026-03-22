package com.agorohov.typoo.article.filter;

import com.agorohov.shared.logging.HttpLogHelper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
@Order(1)
public class RequestLoggingFilter extends OncePerRequestFilter {

    private static final int CONTENT_CACHE_LIMIT = 1024 * 64;   // 64 Kb
    private static final Set<String> SHOULD_NOT_FILTER = Set.of(
            "/actuator",
            "/health",
            "/prometheus"
    );

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        var wrappedRequest = new ContentCachingRequestWrapper(request, CONTENT_CACHE_LIMIT);
        var wrappedResponse = new ContentCachingResponseWrapper(response);

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
        String query = request.getQueryString();

        Map<String, List<String>> headers = Collections.list(request.getHeaderNames()).stream()
                .collect(Collectors.toMap(
                        name -> name,
                        name -> Collections.list(request.getHeaders(name))
                ));

        byte[] bodyBytes = request.getContentAsByteArray();
        String contentType = request.getContentType();

        String logMessage = HttpLogHelper.formatRequest(
                method,
                uri,
                query,
                headers,
                bodyBytes,
                contentType
        );

        log.info(logMessage);
    }

    private void logResponse(ContentCachingResponseWrapper response, long startTime) {
        long duration = System.currentTimeMillis() - startTime;

        byte[] bodyBytes = response.getContentAsByteArray();
        String contentType = response.getContentType();
        int status = response.getStatus();

        String logMessage = HttpLogHelper.formatResponse(
                status,
                duration,
                bodyBytes,
                contentType
        );

        log.info(logMessage);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return SHOULD_NOT_FILTER.stream().anyMatch(path::startsWith);
    }
}

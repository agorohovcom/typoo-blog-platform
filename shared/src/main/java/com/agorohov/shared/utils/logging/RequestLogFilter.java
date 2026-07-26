package com.agorohov.shared.utils.logging;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.util.AntPathMatcher;
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
@Order(1)
@RequiredArgsConstructor
public class RequestLogFilter extends OncePerRequestFilter {

    private static final int CONTENT_CACHE_LIMIT = 1024 * 64;   // 64 Kb
    private static final Set<String> SHOULD_NOT_FILTER = Set.of(
            "/actuator",
            "/health",
            "/prometheus"
    );

    private final HttpLogProperties loggingProperties;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    @SuppressWarnings("NullableProblems")
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
            boolean includeBody = !isBodyLoggingExcluded(request.getRequestURI());
            logRequest(wrappedRequest, includeBody);
            logResponse(wrappedResponse, startTime, includeBody);
            wrappedResponse.copyBodyToResponse();
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return SHOULD_NOT_FILTER.stream().anyMatch(path::startsWith)
                || loggingProperties.getExcludePaths().stream().anyMatch(p -> pathMatcher.match(p, path));
    }

    private boolean isBodyLoggingExcluded(String uri) {
        return loggingProperties.getExcludeBodyPaths().stream().anyMatch(p -> pathMatcher.match(p, uri));
    }

    private void logRequest(ContentCachingRequestWrapper request, boolean includeBody) {
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
                contentType,
                includeBody,
                loggingProperties.getMaxBodySize()
        );

        log.info(logMessage);
    }

    private void logResponse(ContentCachingResponseWrapper response, long startTime, boolean includeBody) {
        long duration = System.currentTimeMillis() - startTime;

        byte[] bodyBytes = response.getContentAsByteArray();
        String contentType = response.getContentType();
        int status = response.getStatus();

        String logMessage = HttpLogHelper.formatResponse(
                status,
                duration,
                bodyBytes,
                contentType,
                includeBody,
                loggingProperties.getMaxBodySize()
        );

        log.info(logMessage);
    }
}

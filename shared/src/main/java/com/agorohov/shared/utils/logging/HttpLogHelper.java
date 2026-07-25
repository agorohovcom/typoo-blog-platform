package com.agorohov.shared.utils.logging;

import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public final class HttpLogHelper {
    private HttpLogHelper() {
    }

    private static final String MASKED_MESSAGE = "[MASKED]";
    private static final String EMPTY_BODY_MESSAGE = "[empty body]";
    private static final String CONTENT_SKIPPED_MESSAGE = "[non-text content skipped]";
    private static final String BODY_REDACTED_MESSAGE = "[body redacted by path rule]";
    private static final String TRUNCATED = " [truncated]";
    private static final Set<String> MASKED_HEADER_NAMES = Set.of(
            "Authorization",
            "Cookie",
            "X-Auth-Token",
            "Api-Key"
    );
    private static final Set<String> CONTENT_TYPES = Set.of(
            "application/json",
            "application/problem+json",
            "text/plain",
            "text/plain;charset=UTF-8",
            "text/plain; charset=utf-8"
    );

    public static String formatRequest(
            String method,
            String uri,
            String queryString,
            Map<String, List<String>> originalHeaders,
            byte[] bodyBytes,
            String contentType,
            boolean includeBody,
            int maxBodySize
    ) {
        String query = queryString != null ? "?" + queryString : "";

        Map<String, String> maskedHeaders = maskHeaders(originalHeaders);

        String headerStr = maskedHeaders.entrySet().stream()
                .map(e -> e.getKey() + ": " + e.getValue())
                .collect(Collectors.joining(", "));

        String displayBody = buildDisplayBody(bodyBytes, contentType, includeBody, maxBodySize);

        return """
                -> Request
                method      : %s
                uri         : %s%s
                headers     : %s
                body        : %s
                """.formatted(
                method,
                uri,
                query,
                headerStr,
                displayBody
        );
    }

    public static String formatResponse(
            int status,
            long durationMs,
            byte[] responseBodyBytes,
            String contentType,
            boolean includeBody,
            int maxBodySize
    ) {
        String displayBody = buildDisplayBody(responseBodyBytes, contentType, includeBody, maxBodySize);

        return """
                <- Response
                status      : %d
                time        : %d ms
                body        : %s
                """.formatted(
                status,
                durationMs,
                displayBody
        );
    }

    private static Map<String, String> maskHeaders(Map<String, List<String>> originalHeaders) {
        Map<String, String> masked = new LinkedHashMap<>();
        originalHeaders.forEach((name, values) -> {
            String value = values.isEmpty()
                    ? ""
                    : MASKED_HEADER_NAMES.stream().anyMatch(name.trim()::equalsIgnoreCase)
                    ? MASKED_MESSAGE
                    : String.join(", ", values);
            masked.put(name, value);
        });
        return masked;
    }

    private static String buildDisplayBody(byte[] bytes, String contentType, boolean includeBody, int maxBodySize) {
        if (!includeBody) {
            return BODY_REDACTED_MESSAGE;
        }

        if (bytes == null || bytes.length == 0) {
            return EMPTY_BODY_MESSAGE;
        }

        boolean isLoggableBody = contentType != null
                && CONTENT_TYPES.stream().anyMatch(contentType.trim()::equalsIgnoreCase);

        if (!isLoggableBody) {
            return CONTENT_SKIPPED_MESSAGE + " (" + contentType + ")";
        }

        return previewBody(bytes, maxBodySize);
    }

    private static String previewBody(byte[] bytes, int maxBodySize) {
        int len = Math.min(bytes.length, maxBodySize);
        String text = new String(bytes, 0, len, StandardCharsets.UTF_8);
        return bytes.length > maxBodySize
                ? text + TRUNCATED
                : text;
    }
}

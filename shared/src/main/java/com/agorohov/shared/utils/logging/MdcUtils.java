package com.agorohov.shared.utils.logging;

import org.slf4j.MDC;

import java.util.UUID;

public final class MdcUtils {

    public static final String TRACE_ID = "traceId";

    private MdcUtils() {
    }

    public static String getTraceId() {
        return MDC.get(TRACE_ID);
    }

    public static void setTraceId(String traceId) {
        if (traceId == null || traceId.isBlank()) {
            traceId = generateTraceId();
        }
        MDC.put(TRACE_ID, traceId);
    }

    public static void generateAndSetTraceId() {
        MDC.put(TRACE_ID, generateTraceId());
    }

    public static void clear() {
        MDC.remove(TRACE_ID);
    }

    public static String generateTraceId() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}

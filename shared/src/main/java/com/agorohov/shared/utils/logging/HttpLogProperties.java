package com.agorohov.shared.utils.logging;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Set;

@Getter
@Setter
@ConfigurationProperties(prefix = "logging.http-helper")
public class HttpLogProperties {

    /**
     * Пути (Ant-style), для которых тело запроса/ответа не логируется вообще.
     * Пример: /v1/admin/**, /actuator/**
     */
    private Set<String> excludeBodyPaths = Set.of();

    /**
     * Пути (Ant-style), которые полностью исключаются из логирования.
     */
    private Set<String> excludePaths = Set.of();

    /**
     * Максимальный размер тела в байтах, который попадёт в лог.
     * Если больше — текст будет обрезан и в конце добавлено [truncated].
     */
    private int maxBodySize = 8192;
}
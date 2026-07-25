package com.agorohov.shared.utils.logging;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@AutoConfiguration
@EnableConfigurationProperties(HttpLogProperties.class)
public class HttpLogConfig {
}

package com.agorohov.shared.utils.logging;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
//@ConditionalOnClass(name = "jakarta.servlet.Filter")
// Если понадобится подобный механизм для реактивного стека, нужно будет сделать отдельный конфиг
// с бином WebFilter и ConditionalOnWebApplication.Type.REACTIVE
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@EnableConfigurationProperties(HttpLogProperties.class)
public class HttpLogConfig {

    @Bean
    public RequestTraceIdFilter requestTraceIdFilter() {
        return new RequestTraceIdFilter();
    }

    @Bean
    @ConditionalOnProperty(prefix = "logging.http-helper", name = "enabled", havingValue = "true")
    public RequestLogFilter requestLogFilter(HttpLogProperties logProperties) {
        return new RequestLogFilter(logProperties);
    }
}

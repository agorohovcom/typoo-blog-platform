package com.agorohov.typoo.article.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.web.config.PageableHandlerMethodArgumentResolverCustomizer;

@Configuration
public class PaginationConfig {

    @Bean
    public PageableHandlerMethodArgumentResolverCustomizer pageableCustomizer() {
        return pageableResolver -> {
            pageableResolver.setMaxPageSize(50);
        };
    }
}

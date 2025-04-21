package com.agorohov.typoo.article;

import com.agorohov.shared.utils.DotenvLoader;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ArticleServiceApplication {

    public static void main(String[] args) {
        DotenvLoader.loadEnvironmentVariables();
        SpringApplication.run(ArticleServiceApplication.class, args);
    }
}
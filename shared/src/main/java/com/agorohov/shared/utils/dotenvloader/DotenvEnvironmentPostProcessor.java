package com.agorohov.shared.utils.dotenvloader;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;

public class DotenvEnvironmentPostProcessor implements EnvironmentPostProcessor {

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        BannerPrinter.printBanner();
        System.out.println("DotenvEnvironmentPostProcessor: Starting");
        DotenvLoader.loadEnvironmentVariables(environment);
        System.out.println("DotenvEnvironmentPostProcessor: Finished");
    }
}

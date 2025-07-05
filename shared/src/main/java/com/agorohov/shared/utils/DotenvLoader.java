package com.agorohov.shared.utils;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

// TODO Удалить вывод в консоль и настроить нормальные логи
public class DotenvLoader {

    /**
     * Загружает переменные окружения из файлов `.env.{profile}`,
     * находящихся в модулях микросервисов,
     * если они не заданы через переменные окружения или -D.
     * Если профиль не задан — по умолчанию `dev`.
     */
    public static void loadEnvironmentVariables(ConfigurableEnvironment environment) {
        String profile = environment.getProperty("spring.profiles.active",
                System.getenv("SPRING_PROFILES_ACTIVE") != null
                        ? System.getenv("SPRING_PROFILES_ACTIVE")
                        : "dev");
        System.setProperty("spring.profiles.active", profile);

//        environment.getPropertySources().addFirst(new PropertySource<>("custom-env") {
//            @Override
//            public Object getProperty(String name) {
//                return System.getProperty(name);
//            }
//        });

        String serviceName = environment.getProperty("spring.application.name",
                System.getenv("SPRING_APPLICATION_NAME"));
        if (serviceName == null || serviceName.isBlank()) {
            System.err.println("ERROR: spring.application.name is not set! Environment: " +
                    environment.getProperty("spring.application.name") + ", Env var: " +
                    System.getenv("SPRING_APPLICATION_NAME"));
            throw new IllegalStateException("spring.application.name must be set");
        }
        System.out.println("!!!!!!!!!!!!!!!!!!! Loading env for service: " + serviceName + ", profile: " + profile);

        Map<String, Object> envProperties = new HashMap<>();
        loadFromFile("services/" + serviceName + "/.env." + profile, serviceName, envProperties);
        // TODO перенести в другой метод (хотя бы loadFromFile)
        environment.getPropertySources().addFirst(new MapPropertySource("dotenv", envProperties));
    }

    /**
     * Загружает переменные из указанного .env-файла, но не перезаписывает уже заданные значения.
     */
    private static void loadFromFile(String fileName, String serviceName, Map<String, Object> envProperties) {
        System.out.println("!!!!!!!!!!!!! Attempting to load file: " + fileName);
        Dotenv dotenv = Dotenv.configure()
                .filename(fileName)
                .ignoreIfMissing()
                .ignoreIfMalformed()
                .load();

        // Проверяем Docker secrets
        String secretFile = "/run/secrets/" + serviceName + "_env_file";
        if (new java.io.File(secretFile).exists()) {
            System.out.println("!!!!!!!!!!!!! Загрузка секретов из: " + secretFile);
            try (BufferedReader reader = new BufferedReader(new FileReader(secretFile))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (!line.trim().isEmpty() && !line.startsWith("#")) {
                        String[] parts = line.split("=", 2);
                        if (parts.length == 2) {
                            String key = parts[0].trim();
                            String value = parts[1].trim();
                            if (!key.isEmpty()) {
                                boolean alreadyDefined = System.getenv(key) != null || System.getProperty(key) != null;
                                if (!alreadyDefined) {
                                    System.setProperty(key, value);
                                    envProperties.put(key, value);
                                    System.out.println("!!!!!!!!!!!!!! Set property: " + key + "=" + value);
                                } else {
                                    System.out.println("!!!!!!!!!!!!!! Пропущено (уже задано): " + key);
                                }
                            }
                        }
                    }
                }
                System.out.println("!!!!!!!!!!!!! Секреты загружены из: " + secretFile);
            } catch (Exception e) {
                System.err.println("!!!!!!!!!!!!! Ошибка загрузки секрета: " + e.getMessage());
            }
        } else {
            System.out.println("!!!!!!!!!!!!! Секрет не найден: " + secretFile);
        }

        // TODO переделать вручную чтобы отказаться от dotenv библиотеки
        dotenv.entries().forEach(entry -> {
            String key = entry.getKey();
            String value = entry.getValue();
            boolean alreadyDefined = System.getenv(key) != null || System.getProperty(key) != null;
            if (!alreadyDefined) {
                System.setProperty(key, value);
                envProperties.put(key, value);
                System.out.println("!!!!!!!!!!!!!! Set property: " + key + "=" + value);
            } else {
                System.out.println("!!!!!!!!!!!!!! Пропущено (уже задано): " + key);
            }
            System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!! Loaded env file: " + fileName + " or secret: " + secretFile);
        });
    }
}

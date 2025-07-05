package com.agorohov.shared.utils;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertySource;

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

        environment.getPropertySources().addFirst(new PropertySource<>("custom-env") {
            @Override
            public Object getProperty(String name) {
                return System.getProperty(name);
            }
        });

        String serviceName = environment.getProperty("spring.application.name",
                System.getenv("SPRING_APPLICATION_NAME"));
        if (serviceName == null || serviceName.isBlank()) {
            System.err.println("ERROR: spring.application.name is not set! Environment: " +
                    environment.getProperty("spring.application.name") + ", Env var: " +
                    System.getenv("SPRING_APPLICATION_NAME"));
            throw new IllegalStateException("spring.application.name must be set");
        }
        System.out.println("!!!!!!!!!!!!!!!!!!! Loading env for service: " + serviceName + ", profile: " + profile);
        loadFromFile("services/" + serviceName + "/.env." + profile);
    }

    /**
     * Загружает переменные из указанного .env-файла, но не перезаписывает уже заданные значения.
     */
    private static void loadFromFile(String fileName) {
        System.out.println("!!!!!!!!!!!!! Attempting to load file: " + fileName);
        Dotenv dotenv = Dotenv.configure()
                .filename(fileName)
                .ignoreIfMissing()
                .ignoreIfMalformed()
                .load();

        dotenv.entries().forEach(entry -> {
            String key = entry.getKey();
            String value = entry.getValue();

            boolean alreadyDefined =
                    System.getenv(key) != null || System.getProperty(key) != null;

            if (!alreadyDefined) {
                System.setProperty(key, value);
                System.out.println("!!!!!!!!!!!!!! Set property: " + key + "=" + value);
            }
            System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!! Loaded env file: " + fileName);
        });
    }

//    // пример без dotenv-java (не тестил)
//    private static void loadFromFile(String filename) {
//        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
//            reader.lines()
//                    .map(String::trim)
//                    .filter(line -> !line.startsWith("#") && line.contains("="))
//                    .forEach(line -> {
//                        String[] parts = line.split("=", 2);
//                        if (parts.length == 2) {
//                            System.setProperty(parts[0].trim(), parts[1].trim());
//                        }
//                    });
//        } catch (IOException ignored) {
//        }
//    }
}

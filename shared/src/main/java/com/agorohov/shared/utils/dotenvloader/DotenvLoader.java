package com.agorohov.shared.utils.dotenvloader;

import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

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

        String serviceName = environment.getProperty("spring.application.name",
                System.getenv("SPRING_APPLICATION_NAME"));
        if (serviceName == null || serviceName.isBlank()) {
            System.err.println("DotenvLoader: spring.application.name is not set! Environment: " +
                    environment.getProperty("spring.application.name") + ", Env var: " +
                    System.getenv("SPRING_APPLICATION_NAME"));
            throw new IllegalStateException("spring.application.name must be set");
        }
        System.out.println("DotenvLoader: Loading env for service: " + serviceName + ", profile: " + profile);

        Map<String, Object> envProperties = new HashMap<>();
        loadFromFile("services/" + serviceName + "/.env." + profile, serviceName, envProperties);
        environment.getPropertySources().addFirst(new MapPropertySource("dotenv", envProperties));
    }

    /**
     * Загружает переменные из указанного .env-файла или Docker secret.
     * Не перезаписывает уже заданные значения.
     */
    private static void loadFromFile(String fileName, String serviceName, Map<String, Object> envProperties) {
        System.out.println("DotenvLoader: Attempting to load file: " + fileName);
        boolean envFileLoaded = loadFile(fileName, envProperties);

        // Проверяем Docker secrets
        String secretFile = "/run/secrets/" + serviceName + "_env_file";
        boolean secretFileLoaded = loadFile(secretFile, envProperties);

        StringBuilder loadedMessage = new StringBuilder("DotenvLoader: Loaded environment variables from: ");
        if (envFileLoaded && secretFileLoaded) {
            loadedMessage.append(fileName).append(" and ").append(secretFile);
        } else if (envFileLoaded) {
            loadedMessage.append(fileName);
        } else if (secretFileLoaded) {
            loadedMessage.append(secretFile);
        } else {
            loadedMessage.append("nothing (no new variables loaded)");
        }
        System.out.println(loadedMessage);
    }

    private static boolean loadFile(String filePath, Map<String, Object> envProperties) {
        boolean fileLoaded = false;
        if (new java.io.File(filePath).exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
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
                                    fileLoaded = true;
                                }
                            }
                        }
                    }
                }
                System.out.println("DotenvLoader: Loaded file: " + filePath);
            } catch (Exception e) {
                System.err.println("DotenvLoader: Failed to load file: " + filePath + ", error: " + e.getMessage());
            }
        } else {
            System.out.println("DotenvLoader: File not found: " + filePath);
        }
        return fileLoaded;
    }
}

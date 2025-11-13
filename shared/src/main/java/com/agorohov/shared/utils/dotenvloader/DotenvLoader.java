package com.agorohov.shared.utils.dotenvloader;

import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DotenvLoader {

    /**
     * Загружает переменные окружения из файлов `.env.{profile}`,
     * находящихся в модулях микросервисов,
     * если они не заданы через переменные окружения или -D.
     * Если профиль не задан — по умолчанию `dev`.
     */
    public static void loadEnvironmentVariables(ConfigurableEnvironment environment) {
        if (!environment.getProperty("dotenv.enabled", Boolean.class, true)) {
            System.out.println("DotenvLoader: Disabled via configuration");
            return;
        }

        String profile = getActiveProfile(environment);
        String serviceName = getServiceName(environment);

        System.out.println("DotenvLoader: Loading env for service: " + serviceName + ", profile: " + profile);

        DotenvConfig config = createConfig(environment, serviceName, profile);

        Map<String, Object> envProperties = new HashMap<>();
        loadFromConfig(config, envProperties);

        environment.getPropertySources().addFirst(new MapPropertySource("dotenv", envProperties));
    }

    private static String getActiveProfile(ConfigurableEnvironment environment) {
        String profile = environment.getProperty("spring.profiles.active",
                System.getenv("SPRING_PROFILES_ACTIVE") != null
                        ? System.getenv("SPRING_PROFILES_ACTIVE")
                        : "dev");
        System.setProperty("spring.profiles.active", profile);
        return profile;
    }

    private static String getServiceName(ConfigurableEnvironment environment) {
        String serviceName = environment.getProperty("spring.application.name",
                System.getenv("SPRING_APPLICATION_NAME"));
        if (serviceName == null || serviceName.isBlank()) {
            System.err.println("DotenvLoader: spring.application.name is not set! Environment: " +
                    environment.getProperty("spring.application.name") + ", Env var: " +
                    System.getenv("SPRING_APPLICATION_NAME"));
            throw new IllegalStateException("spring.application.name must be set");
        }
        return serviceName;
    }

    private static DotenvConfig createConfig(ConfigurableEnvironment environment, String serviceName, String profile) {
        // Базовый путь (по умолчанию: services/{service})
        String basePath = environment.getProperty("dotenv.base-path", "services/{service}")
                .replace("{service}", serviceName)
                .replace("${spring.application.name}", serviceName);

        // Имя файла (по умолчанию: .env.{profile})
        String filename = environment.getProperty("dotenv.filename", ".env.{profile}")
                .replace("{profile}", profile)
                .replace("${spring.profiles.active}", profile);

        // Полный путь к файлу
        String fullPath = basePath + File.separator + filename;

        List<String> filePatterns = getFilePatterns(environment, fullPath);

        String secretsPath = environment.getProperty("dotenv.secrets-path", "/run/secrets");
        boolean overrideExisting = environment.getProperty("dotenv.override-existing", Boolean.class, false);

        return new DotenvConfig(serviceName, profile, basePath, filename, filePatterns, secretsPath, overrideExisting);
    }

    private static List<String> getFilePatterns(ConfigurableEnvironment environment, String defaultFullPath) {
        // Если указан file-patterns, используем его
        String patternsProperty = environment.getProperty("dotenv.file-patterns");
        if (patternsProperty != null) {
            // ЕСЛИ ПУСТАЯ СТРОКА - ВОЗВРАЩАЕМ ПУСТОЙ СПИСОК
            if (patternsProperty.trim().isEmpty()) {
                return Collections.emptyList();
            }
            try {
                List<String> patterns = parsePatterns(patternsProperty);
                return patterns.stream()
                        .map(pattern -> pattern
                                .replace("{service}", environment.getProperty("spring.application.name"))
                                .replace("{profile}", environment.getProperty("spring.profiles.active", "dev"))
                                .replace("${spring.application.name}", environment.getProperty("spring.application.name"))
                                .replace("${spring.profiles.active}", environment.getProperty("spring.profiles.active", "dev")))
                        .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
            } catch (Exception e) {
                System.err.println("DotenvLoader: Failed to parse file-patterns, using default path: " + e.getMessage());
            }
        }

        // Иначе используем сгенерированный полный путь
        return Collections.singletonList(defaultFullPath);
    }

    private static List<String> parsePatterns(String patternsProperty) {
        if (patternsProperty.startsWith("[") && patternsProperty.endsWith("]")) {
            String content = patternsProperty.substring(1, patternsProperty.length() - 1);
            return Arrays.stream(content.split(","))
                    .map(String::trim)
                    .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
        }
        return Collections.singletonList(patternsProperty);
    }

    private static void loadFromConfig(DotenvConfig config, Map<String, Object> envProperties) {
        boolean anyLoaded = false;

        for (String filePattern : config.getFilePatterns()) {
            String resolvedPath = resolvePath(filePattern, config);
            System.out.println("DotenvLoader: Attempting to load file: " + resolvedPath);
            boolean loaded = loadFile(resolvedPath, config, envProperties);
            if (loaded) {
                anyLoaded = true;
                System.out.println("DotenvLoader: Successfully loaded from: " + resolvedPath);
            }
        }

        String secretPath = config.getSecretsPath() + "/" + config.getServiceName() + "_env_file";
        System.out.println("DotenvLoader: Attempting to load secret: " + secretPath);
        boolean secretLoaded = loadFile(secretPath, config, envProperties);
        if (secretLoaded) {
            anyLoaded = true;
            System.out.println("DotenvLoader: Successfully loaded from secret: " + secretPath);
        }

        if (!anyLoaded) {
            System.out.println("DotenvLoader: No environment variables loaded from any source");
        }
    }

    private static String resolvePath(String pattern, DotenvConfig config) {
        return pattern
                .replace("{service}", config.getServiceName())
                .replace("{profile}", config.getProfile())
                .replace("{basePath}", config.getBasePath())
                .replace("{filename}", config.getFilename());
    }

    private static boolean loadFile(String filePath, DotenvConfig config, Map<String, Object> envProperties) {
        boolean fileLoaded = false;
        java.io.File file = new java.io.File(filePath);

        System.out.println("DotenvLoader: Checking file: " + filePath);
        System.out.println("DotenvLoader: File exists: " + file.exists());
        System.out.println("DotenvLoader: Absolute path: " + file.getAbsolutePath());
        System.out.println("DotenvLoader: Can read: " + file.canRead());

        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (!line.trim().isEmpty() && !line.startsWith("#")) {
                        String[] parts = line.split("=", 2);
                        if (parts.length == 2) {
                            String key = parts[0].trim();
                            String value = parts[1].trim();
                            if (!key.isEmpty()) {
                                boolean alreadyDefined = System.getenv(key) != null || System.getProperty(key) != null;
                                if (!alreadyDefined || config.isOverrideExisting()) {
                                    System.setProperty(key, value);
                                    envProperties.put(key, value);
                                    fileLoaded = true;
                                    System.out.println("DotenvLoader: Loaded variable: " + key);
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
                System.err.println("DotenvLoader: Failed to load file: " + filePath + ", error: " + e.getMessage());
            }
        } else {
            System.out.println("DotenvLoader: File not found: " + filePath);
        }
        return fileLoaded;
    }

    private static class DotenvConfig {
        private final String serviceName;
        private final String profile;
        private final String basePath;
        private final String filename;
        private final List<String> filePatterns;
        private final String secretsPath;
        private final boolean overrideExisting;

        public DotenvConfig(String serviceName, String profile, String basePath, String filename,
                            List<String> filePatterns, String secretsPath, boolean overrideExisting) {
            this.serviceName = serviceName;
            this.profile = profile;
            this.basePath = basePath;
            this.filename = filename;
            this.filePatterns = filePatterns;
            this.secretsPath = secretsPath;
            this.overrideExisting = overrideExisting;
        }

        public String getServiceName() {
            return serviceName;
        }

        public String getProfile() {
            return profile;
        }

        public String getBasePath() {
            return basePath;
        }

        public String getFilename() {
            return filename;
        }

        public List<String> getFilePatterns() {
            return filePatterns;
        }

        public String getSecretsPath() {
            return secretsPath;
        }

        public boolean isOverrideExisting() {
            return overrideExisting;
        }
    }
}

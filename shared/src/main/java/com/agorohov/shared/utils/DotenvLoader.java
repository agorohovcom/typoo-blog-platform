package com.agorohov.shared.utils;

import io.github.cdimascio.dotenv.Dotenv;

public class DotenvLoader {

    /**
     * Загружает переменные окружения из файлов `.env` и `.env.{profile}`,
     * если они не заданы через переменные окружения или -D.
     * Если профиль не задан — по умолчанию dev.
     */
    public static void loadEnvironmentVariables() {
        String profile = System.getProperty("spring.profiles.active");
        if (profile == null || profile.isBlank()) {
            profile = System.getenv("SPRING_PROFILES_ACTIVE");
        }

        if (profile == null || profile.isBlank()) {
            profile = "dev";
            System.setProperty("spring.profiles.active", profile);
        }

//        loadFromFile(".env");
        loadFromFile(".env." + profile);
    }

    /**
     * Загружает переменные из указанного .env-файла, но не перезаписывает уже заданные значения.
     */
    private static void loadFromFile(String fileName) {
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
            }
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

package com.rubenmarin.documentservice.support;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Function;

/**
 * 	Recuperar las credenciales de forma segura.
 */
public final class TestEnvironment {

    private TestEnvironment() {    }

    public static String get(String key) {
        return get(
                key,
                variableName -> System.getenv(variableName),
                Path.of("").toAbsolutePath()
        );
    }

    static String get(String key, Function<String, String> environment, Path directory) {

        String value = environment.apply(key);

        if (value != null && !value.isBlank()) {
            return value;
        }

        while (directory != null) {

            Path envFile = directory.resolve(".env");

            if (Files.isRegularFile(envFile)) {
                try {
                    for (String line : Files.readAllLines(envFile)) {

                        String entry = line.strip();

                        if (entry.startsWith(key + "=")) {

                            String result = entry
                                    .substring(key.length() + 1)
                                    .strip();

                            if (!result.isBlank()) {
                                return result;
                            }
                        }
                    }
                } catch (IOException e) {
                    throw new IllegalStateException("Unable to read .env file", e);
                }
            }
            directory = directory.getParent();
        }
        throw new IllegalStateException("Missing required environment variable: " + key);
    }
}
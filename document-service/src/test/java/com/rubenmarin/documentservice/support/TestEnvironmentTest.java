package com.rubenmarin.documentservice.support;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.UUID;


class TestEnvironmentTest {

    @TempDir
    Path tempDir;

    private static final String KEY =
            "TEST_TOKEN_" + UUID.randomUUID().toString().replace("-", "");

    @Test
    void shouldReturnExistingEnvironmentVariable() {

        Map<String, String> environment = Map.of(KEY, "environment-token");

        String result = TestEnvironment.get(
                KEY,
                variableName -> environment.get(variableName),
                tempDir
        );

        Assertions.assertEquals("environment-token", result);
    }

    @Test
    void shouldReadValueFromEnvFile() throws IOException {

        Files.writeString(tempDir.resolve(".env"), KEY + "=file-token");

        String result = TestEnvironment.get(KEY, variableName -> null, tempDir);

        Assertions.assertEquals("file-token", result);
    }

    @Test
    void shouldPrioritizeEnvironmentVariableOverEnvFile() throws IOException {

        Files.writeString(tempDir.resolve(".env"), KEY + "=file-token");

        Map<String, String> environment = Map.of(KEY, "environment-token");

        String result = TestEnvironment.get(KEY, variableName -> environment.get(variableName), tempDir);

        Assertions.assertEquals("environment-token", result);
    }

    @Test
    void shouldFindEnvFileInParentDirectory() throws IOException {

        Files.writeString(tempDir.resolve(".env"), KEY + "=parent-token");

        Path childDirectory = Files.createDirectories(tempDir.resolve("document-service"));

        String result = TestEnvironment.get(
                KEY,
                variableName -> null,
                childDirectory
        );

        Assertions.assertEquals("parent-token", result);
    }

    @Test
    void shouldReadEnvFileWhenEnvironmentVariableIsBlank() throws IOException {

        Files.writeString(tempDir.resolve(".env"), KEY + "=file-token");

        String result = TestEnvironment.get(KEY, variableName -> "   ", tempDir);

        Assertions.assertEquals("file-token", result);
    }

    @Test
    void shouldThrowExceptionWhenVariableIsMissing() {

        IllegalStateException exception = Assertions.assertThrows(
                IllegalStateException.class,
                () -> TestEnvironment.get(
                        KEY,
                        variableName -> null,
                        tempDir
                )
        );

        Assertions.assertEquals(
                "Missing required environment variable: " + KEY,
                exception.getMessage()
        );
    }

    @Test
    void shouldThrowExceptionWhenEnvFileValueIsEmpty() throws IOException {

        Files.writeString(tempDir.resolve(".env"), KEY + "=");

        Assertions.assertThrows(
                IllegalStateException.class,
                () -> TestEnvironment.get(
                        KEY,
                        variableName -> null,
                        tempDir
                )
        );
    }
}
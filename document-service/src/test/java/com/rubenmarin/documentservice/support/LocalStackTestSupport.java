package com.rubenmarin.documentservice.support;


import com.rubenmarin.documentservice.infrastructure.config.AwsConfig;
import com.rubenmarin.documentservice.infrastructure.config.AwsProperties;
import org.testcontainers.localstack.LocalStackContainer;
import org.testcontainers.utility.DockerImageName;
import software.amazon.awssdk.services.s3.S3Client;

/**
 * Centralizar la configuración del contenedor y del cliente AWS para los tests.
 */
public final class LocalStackTestSupport {

    private static final String IMAGE = "localstack/localstack:2026.11.0";

    private LocalStackTestSupport() {
    }

    public static LocalStackContainer createContainer() {

        String authToken = TestEnvironment.get("LOCALSTACK_AUTH_TOKEN");

        return new LocalStackContainer(DockerImageName.parse(IMAGE))
                .withServices("s3")
                .withEnv("LOCALSTACK_AUTH_TOKEN", authToken);
    }

    public static AwsProperties createAwsProperties(LocalStackContainer container, String bucket) {
        return new AwsProperties(
                container.getRegion(),
                container.getEndpoint().toString(),
                container.getAccessKey(),
                container.getSecretKey(),
                new AwsProperties.S3(bucket, true)
        );
    }

    public static S3Client createS3Client(AwsProperties properties) {
        return new AwsConfig().s3Client(properties);
    }
}
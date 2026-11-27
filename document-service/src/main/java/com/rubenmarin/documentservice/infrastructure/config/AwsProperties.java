package com.rubenmarin.documentservice.infrastructure.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

// Spring Boot utiliza relaxed binding para relacionar las propiedades YAML con los atributos Java.
@ConfigurationProperties(prefix = "aws")
public record AwsProperties(
        String region,
        String endpoint,
        String accessKey,
        String secretKey,
        S3 s3
) {
    public record S3(
            String bucket,
            boolean pathStyleAccess
    ) {}
}
package com.rubenmarin.documentservice.infrastructure.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

import java.net.URI;

@Configuration
@Profile("local") // Activa esta configuración únicamente en el entorno local.
@EnableConfigurationProperties(AwsProperties.class) // Registra AwsProperties como bean de Spring.
public class AwsConfig {

    @Bean // Registra S3Client en el ApplicationContext.
    public S3Client s3Client(AwsProperties properties) {
        return S3Client.builder()
                .region(Region.of(properties.region()))
                .endpointOverride(URI.create(properties.endpoint())) //	Redirige las peticiones a LocalStack en lugar de AWS real.
                .credentialsProvider(                                // Proporciona las credenciales para firmar las peticiones.
                        StaticCredentialsProvider.create(
                                AwsBasicCredentials.create(
                                        properties.accessKey(),
                                        properties.secretKey()
                                )   // En producción no usar StaticCredentialsProvider con credenciales escritas en el YAML.
                                    // Utilizar DefaultCredentialsProvider y roles IAM.
                        )
                )
                .forcePathStyle(properties.s3().pathStyleAccess()) // Fuerza el direccionamiento del bucket mediante la ruta de la URL.
                .build();
    }
}
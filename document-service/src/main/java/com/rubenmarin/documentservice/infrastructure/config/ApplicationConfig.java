package com.rubenmarin.documentservice.infrastructure.config;

import com.rubenmarin.documentservice.application.port.in.UploadDocumentUseCase;
import com.rubenmarin.documentservice.application.port.out.DocumentStoragePort;
import com.rubenmarin.documentservice.application.service.DocumentService;
import com.rubenmarin.documentservice.infrastructure.adapter.out.S3StorageAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
@Profile("local")
public class ApplicationConfig {

    @Bean
    public UploadDocumentUseCase uploadDocumentUseCase(DocumentStoragePort documentStoragePort) {
        return new DocumentService(documentStoragePort);
    }

    @Bean
    public DocumentStoragePort documentStoragePort(S3Client s3Client, AwsProperties awsProperties) {
        return new S3StorageAdapter(s3Client, awsProperties);
    }
}

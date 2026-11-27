
package com.rubenmarin.documentservice.infrastructure.adapter.out;

import com.rubenmarin.documentservice.infrastructure.config.AwsProperties;
import com.rubenmarin.documentservice.support.LocalStackTestSupport;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.localstack.LocalStackContainer;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

@Testcontainers
class S3StorageAdapterIntegrationTest {

    private static final String BUCKET = "document-platform";

    @Container
    private static final LocalStackContainer localStack = LocalStackTestSupport.createContainer();

    private static S3Client s3Client;
    private static S3StorageAdapter adapter;

    @BeforeAll
    static void setUp() {

        AwsProperties properties = LocalStackTestSupport.createAwsProperties(localStack, BUCKET);

        s3Client = LocalStackTestSupport.createS3Client(properties);

        s3Client.createBucket(
                CreateBucketRequest.builder()
                        .bucket(BUCKET)
                        .build()
        );

        adapter = new S3StorageAdapter(s3Client, properties);
    }

    @AfterAll
    static void tearDown() {
        if (s3Client != null) {
            s3Client.close();
        }
    }

    @Test
    void shouldUploadAndStoreDocumentInS3() {

        // Given
        String key = "documents/123/invoice.txt";
        String contentType = "text/plain";

        byte[] originalContent = "Test document stored in S3".getBytes(StandardCharsets.UTF_8);

        // When
        adapter.upload(
                key,
                contentType,
                originalContent.length,
                new ByteArrayInputStream(originalContent)
        );

        // Then
        GetObjectRequest request = GetObjectRequest.builder()
                .bucket(BUCKET)
                .key(key)
                .build();

        byte[] storedContent = s3Client.getObjectAsBytes(request).asByteArray();

        Assertions.assertArrayEquals(originalContent, storedContent);
    }
}
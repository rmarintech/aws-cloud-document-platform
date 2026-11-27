package com.rubenmarin.documentservice.infrastructure.adapter.out;

import com.rubenmarin.documentservice.infrastructure.config.AwsProperties;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.http.SdkHttpResponse;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;


class S3StorageAdapterTest {

    private S3Client s3Client;
    private S3StorageAdapter adapter;

    @BeforeEach
    void setUp() {
        s3Client = Mockito.mock(S3Client.class);

        AwsProperties properties = new AwsProperties(
                "us-east-1",
                "http://localhost:4566",
                "test",
                "test",
                new AwsProperties.S3("document-platform", true)
        );

        adapter = new S3StorageAdapter(s3Client, properties);
    }

    @Test
    void shouldUploadDocumentToS3() {

        // Given
        String key = "documents/123/invoice.pdf";
        String contentType = "application/pdf";
        byte[] data = "Test document".getBytes(StandardCharsets.UTF_8);

        PutObjectResponse.Builder responseBuilder = PutObjectResponse.builder()
                .eTag("test-etag");

        responseBuilder.sdkHttpResponse(
                SdkHttpResponse.builder()
                        .statusCode(200)
                        .build()
        );

        PutObjectResponse response = responseBuilder.build();

        Mockito.when(s3Client.putObject(
                Mockito.any(PutObjectRequest.class),
                Mockito.any(RequestBody.class)
        )).thenReturn(response);

        // When
        adapter.upload(key, contentType, data.length, new ByteArrayInputStream(data));

        // Then
        ArgumentCaptor<PutObjectRequest> requestCaptor = ArgumentCaptor.forClass(PutObjectRequest.class);

        ArgumentCaptor<RequestBody> bodyCaptor = ArgumentCaptor.forClass(RequestBody.class);

        Mockito.verify(s3Client, Mockito.times(1)).putObject(
                requestCaptor.capture(),
                bodyCaptor.capture()
        );

        PutObjectRequest request = requestCaptor.getValue();

        Assertions.assertEquals("document-platform", request.bucket());
        Assertions.assertEquals(key, request.key());
        Assertions.assertEquals(contentType, request.contentType());
        Assertions.assertEquals((long) data.length, request.contentLength());

        Assertions.assertEquals(
                data.length,
                bodyCaptor.getValue().optionalContentLength().orElseThrow()
        );
    }

    @Test
    void shouldPropagateExceptionWhenS3UploadFails() {

        // Given
        String key = "documents/123/invoice.pdf";
        String contentType = "application/pdf";
        byte[] data = "Test document".getBytes(StandardCharsets.UTF_8);

        S3Exception exception = Mockito.mock(S3Exception.class);

        Mockito.when(exception.statusCode()).thenReturn(503);

        Mockito.when(s3Client.putObject(
                Mockito.any(PutObjectRequest.class),
                Mockito.any(RequestBody.class)
        )).thenThrow(exception);

        // When
        S3Exception result = Assertions.assertThrows(
                S3Exception.class,
                () -> adapter.upload(
                        key,
                        contentType,
                        data.length,
                        new ByteArrayInputStream(data)
                )
        );

        // Then
        Assertions.assertSame(exception, result);
        Assertions.assertEquals(503, result.statusCode());

        Mockito.verify(s3Client, Mockito.times(1)).putObject(
                Mockito.any(PutObjectRequest.class),
                Mockito.any(RequestBody.class)
        );
    }
}
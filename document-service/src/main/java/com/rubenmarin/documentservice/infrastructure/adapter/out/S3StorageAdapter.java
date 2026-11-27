package com.rubenmarin.documentservice.infrastructure.adapter.out;

import com.rubenmarin.documentservice.application.port.out.DocumentStoragePort;
import com.rubenmarin.documentservice.infrastructure.config.AwsProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import java.io.InputStream;

public class S3StorageAdapter implements DocumentStoragePort {

    private static final Logger log = LoggerFactory.getLogger(S3StorageAdapter.class);
    private final S3Client s3Client;
    private final AwsProperties awsProperties;

    public S3StorageAdapter(S3Client s3Client, AwsProperties awsProperties) {
        this.s3Client = s3Client;
        this.awsProperties = awsProperties;
    }

    @Override
    public void upload(String key, String contentType, long contentLength, InputStream content) {

        String bucket = awsProperties.s3().bucket();
        PutObjectRequest putObjectRequest = PutObjectRequest
                .builder()
                .bucket(bucket)
                .key(key)
                .contentType(contentType)
                .contentLength(contentLength)
                .build();

        RequestBody requestBody = RequestBody.fromInputStream(content, contentLength);

        PutObjectResponse putObjectResponse = s3Client.putObject(putObjectRequest, requestBody);

        String bucketKey =bucket + "/" + key;
        String versionId = putObjectResponse.versionId();
        String eTag = putObjectResponse.eTag();
        String serverSideEncryption = putObjectResponse.serverSideEncryptionAsString();
        int responseStatusCode = putObjectResponse.sdkHttpResponse().statusCode();

        log.info("Uploaded: buketKey: {} , version: {} , eTag: {} , serverSideEncryption: {} , responseStatusCode: {}",
                bucketKey, versionId, eTag, serverSideEncryption, responseStatusCode);


    }
}

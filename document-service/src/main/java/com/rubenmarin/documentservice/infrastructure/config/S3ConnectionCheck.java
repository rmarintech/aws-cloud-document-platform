package com.rubenmarin.documentservice.infrastructure.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.s3.S3Client;

@Component
@Profile("local")
public class S3ConnectionCheck implements CommandLineRunner {

    private final S3Client s3Client;

    public S3ConnectionCheck(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    @Override
    public void run(String... args) {
        s3Client.listBuckets()
                .buckets()
                .forEach(bucket ->
                        System.out.println(bucket.name()));
    }
}
package com.playzone.pems.infrastructure.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;

import java.net.URI;

@Configuration
public class StorageConfig {

    @Value("${playzone.storage.access-key}")
    private String accessKey;

    @Value("${playzone.storage.secret-key}")
    private String secretKey;

    @Value("${playzone.storage.region:us-east-1}")
    private String region;

    @Value("${playzone.storage.endpoint:#{null}}")
    private String endpoint;

    @Bean
    public S3Client s3Client() {
        var credentials = AwsBasicCredentials.create(accessKey, secretKey);

        var builder = S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(credentials));

        if (endpoint != null && !endpoint.isBlank()) {
            builder.endpointOverride(URI.create(endpoint));
            // Path-style required for local S3 emulators (LocalStack/MinIO);
            // virtual-hosted-style would prepend the bucket name to the hostname.
            builder.serviceConfiguration(
                S3Configuration.builder().pathStyleAccessEnabled(true).build()
            );
        }

        return builder.build();
    }
}
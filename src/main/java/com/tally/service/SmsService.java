package com.tally.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;
import software.amazon.awssdk.services.sns.model.PublishResponse;

@Service
public class SmsService {

    @Value("${aws.sns.region}")
    private String region;

    @Value("${aws.sns.access-key}")
    private String accessKey;

    @Value("${aws.sns.secret-key}")
    private String secretKey;

    private SnsClient getSnsClient() {
        AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKey, secretKey);
        return SnsClient.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .build();
    }

    public boolean sendSms(String phoneNumber, String message) {
        try (SnsClient snsClient = getSnsClient()) {
            PublishRequest request = PublishRequest.builder()
                    .phoneNumber(phoneNumber)
                    .message(message)
                    .build();

            PublishResponse response = snsClient.publish(request);
            return response.messageId() != null;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
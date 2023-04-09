package com.hybe.larva.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Slf4j
@ToString
@Getter
@RequiredArgsConstructor
@Component
public class AppInfo {

    @ToString.Exclude
    private final Environment environment;

    // spring.profiles.active
    private String profile;
    // spring.data.mongodb.database
    private String database;
    // spring.cloud.gcp.credentials.location.classpath
    private String firebaseConfig;

    // larva.version
    private String version;
    // larva.license
    private String license;
    // larva.api
    private String api;

    // cloud.aws.s3.bucket
    private String bucket;

    private String clientBucket;

    //    String distributionDomain = "dty4ildx1lf58.cloudfront.net";
    private String cloudFrontDomain;

    //    String keyPairId = "K2SYFO30YJ3CCE";
    private String cloudFrontKeyPairId;

    // ClassPathResource privateKeyResource = new ClassPathResource("private_key.der");
    private String cloudFrontKeyPath;




    @PostConstruct
    public void init() {
        // spring
        profile = environment.getProperty("spring.profiles.active");
        database = environment.getProperty("spring.data.mongodb.database");
        firebaseConfig = environment.getProperty("spring.cloud.gcp.credentials.location.classpath");

        // larva
        version = environment.getProperty("larva.version");
        license = environment.getProperty("larva.license");
        api = environment.getProperty("larva.api");

        // aws
        bucket = environment.getProperty("cloud.aws.s3.bucket");
        clientBucket = environment.getProperty("cloud.aws.s3.client-bucket");


        cloudFrontDomain = environment.getProperty("cloud.aws.cloud-front.domain");
        cloudFrontKeyPairId = environment.getProperty("cloud.aws.cloud-front.pairId");
        cloudFrontKeyPath = environment.getProperty("cloud.aws.cloud-front.path");

        log.info("### app info: {}", this);
    }
}

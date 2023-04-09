package com.hybe.larva;

import net.javacrumbs.shedlock.spring.annotation.EnableSchedulerLock;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.TimeZone;

@EnableCaching
@EnableRetry
@EnableSchedulerLock(defaultLockAtMostFor = "10m")
@EnableScheduling
@EnableBatchProcessing
@EnableAspectJAutoProxy
@EnableMongoAuditing
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class LarvaApplication {

    public static void main(String[] args) {
        System.out.println("### FIXME: set default timezone to UTC ###");
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        System.out.println("### FIXME: timezone=" + TimeZone.getDefault() + " ###");

        SpringApplication.run(LarvaApplication.class, args);
    }
}

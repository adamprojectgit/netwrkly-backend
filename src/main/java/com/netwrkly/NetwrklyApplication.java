package com.netwrkly;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan(basePackages = {
    "com.netwrkly.auth.model",
    "com.netwrkly.brief.model",
    "com.netwrkly.profile.model"
})
@EnableJpaRepositories(basePackages = {
    "com.netwrkly.auth.repository",
    "com.netwrkly.brief.repository",
    "com.netwrkly.profile.repository"
})
public class NetwrklyApplication {
    public static void main(String[] args) {
        SpringApplication.run(NetwrklyApplication.class, args);
    }
} 
package com.bitjawsbill;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.bitjawsbill.repository")
@EntityScan(basePackages = "com.bitjawsbill.model")
@EnableTransactionManagement
public class BitjawsbillApplication {

    public static void main(String[] args) {
        SpringApplication.run(BitjawsbillApplication.class, args);
    }
} 
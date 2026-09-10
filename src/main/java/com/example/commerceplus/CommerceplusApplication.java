package com.example.commerceplus;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class CommerceplusApplication {

    public static void main(String[] args) {
        SpringApplication.run(CommerceplusApplication.class, args);
    }

}

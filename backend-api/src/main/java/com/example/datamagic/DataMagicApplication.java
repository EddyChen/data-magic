package com.example.datamagic;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class DataMagicApplication {

    public static void main(String[] args) {
        SpringApplication.run(DataMagicApplication.class, args);
    }
}

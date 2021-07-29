package com.example.tradebot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

//@EnableJpaRepositories
@EnableAsync
@SpringBootApplication
//@Configuration
public class ServingWebContentApplication {


    public static void main(String[] args) {
        SpringApplication.run(ServingWebContentApplication.class, args);

    }

}

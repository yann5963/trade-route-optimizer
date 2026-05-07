package com.example.mtg.market;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MtgMarketServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(MtgMarketServiceApplication.class, args);
    }
}

package com.example.it214_ss14_bai6;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan(basePackages = {"com.example.it214_ss14_bai6", "org.springframework.statemachine.data.jpa"})
@EnableJpaRepositories(basePackages = {"com.example.it214_ss14_bai6", "org.springframework.statemachine.data.jpa"})
public class It214Ss14Bai6Application {

    public static void main(String[] args) {
        SpringApplication.run(It214Ss14Bai6Application.class, args);
    }

}

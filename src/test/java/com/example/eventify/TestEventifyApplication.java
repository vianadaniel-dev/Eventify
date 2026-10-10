package com.example.eventify;

import org.springframework.boot.SpringApplication;

public class TestEventifyApplication {

    public static void main(String[] args) {
        SpringApplication.from(EventifyApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}

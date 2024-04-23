package com.reynolds.composer.v1.composerfacade;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@ComponentScan("com.reynolds.composer.v1")
public class ComposerFacadeApplication {

    public static void main(String[] args) {
        SpringApplication.run(ComposerFacadeApplication.class, args);
    }

    @Bean
    RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
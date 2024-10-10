package com.example.generateurformulaire;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.example.generateurformulaire")
public class GenerateurForlulaireApplication {

    public static void main(String[] args) {
        SpringApplication.run(GenerateurForlulaireApplication.class, args);
    }

}

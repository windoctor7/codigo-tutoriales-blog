package com.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class DemoApplication {

    @Autowired
    PrimerEjemplo ejemplo;

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

	@Bean
	public CommandLineRunner commandLineRunner(){
        return new CommandLineRunner() {
            @Override
            public void run(String... args) throws Exception {
                ejemplo.imprimir();
            }
        };
    }
}

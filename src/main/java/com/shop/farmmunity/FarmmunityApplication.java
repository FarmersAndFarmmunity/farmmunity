package com.shop.farmmunity;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class FarmmunityApplication {
	public static void main(String[] args) {
		SpringApplication.run(FarmmunityApplication.class, args);
	}
}

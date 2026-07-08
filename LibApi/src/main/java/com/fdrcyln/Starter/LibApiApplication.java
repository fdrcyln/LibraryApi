package com.fdrcyln.Starter;

import org.springframework.boot.SpringApplication;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;


@SpringBootApplication(scanBasePackages = "com.fdrcyln")
@EntityScan(basePackages = "com.fdrcyln")
@EnableJpaRepositories(basePackages = "com.fdrcyln.repository")
public class LibApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(LibApiApplication.class, args);
	}

}

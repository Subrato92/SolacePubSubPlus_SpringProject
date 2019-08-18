package com.subrato.packages.solace_app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan
public class SolaceAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(SolaceAppApplication.class, args);
	}

}

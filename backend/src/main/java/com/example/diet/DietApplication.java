package com.example.diet;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DietApplication {

	public static void main(String[] args) {
		System.out.println("<------ Application runnning ----->>");
		SpringApplication.run(DietApplication.class, args);
	}

}

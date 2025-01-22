package com.example.GraduationProject;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class GraduationProjectApplication {
	public static void main(String[] args) {
		SpringApplication.run(GraduationProjectApplication.class, args);
	}
}

package com.example.mailapp1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class Mailapp1Application {

	public static void main(String[] args) {
		SpringApplication.run(Mailapp1Application.class, args);
	}

}

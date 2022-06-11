package com.hugos.BanKING;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
public class BanKINGApplication {
	public static void main(String[] args) {
		SpringApplication.run(BanKINGApplication.class, args);
	}

}

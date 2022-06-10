package com.hugos.BanKING;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class BanKINGApplication {
	public static void main(String[] args) {
		SpringApplication.run(BanKINGApplication.class, args);
	}

	@Bean
	public void CommandRunner() {

	}

}

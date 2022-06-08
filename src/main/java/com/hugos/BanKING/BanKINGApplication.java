package com.hugos.BanKING;

import com.hugos.BanKING.domain.User;
import com.hugos.BanKING.domain.Role;
import com.hugos.BanKING.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;

@SpringBootApplication
public class BanKINGApplication {

	public static void main(String[] args) {
		SpringApplication.run(BanKINGApplication.class, args);
	}

	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	CommandLineRunner run(UserService userService) {
		return args -> {
			userService.saveRole(new Role(null, "ROLE_USER"));
			userService.saveRole(new Role(null, "ROLE_MANAGER"));
			userService.saveRole(new Role(null, "ROLE_ADMIN"));
			userService.saveRole(new Role(null, "ROLE_SUPER_ADMIN"));

			userService.saveUser(new User(null, "John trevota", "John", "1234", false, new ArrayList<>()));
			userService.saveUser(new User(null, "Hugo Korte", "Hugos68", "321", true, new ArrayList<>()));
			userService.saveUser(new User(null, "Test Demo", "Testerdemo", "1337", false, new ArrayList<>()));

			userService.addRoleToUser("John", "ROLE_USER");
			userService.addRoleToUser("Hugos68", "ROLE_MANAGER");
			userService.addRoleToUser("Hugos68", "ROLE_USER");
			userService.addRoleToUser("Hugos68", "ROLE_SUPER_ADMIN");
			userService.addRoleToUser("Testerdemo", "ROLE_USER");



		};
	}

}

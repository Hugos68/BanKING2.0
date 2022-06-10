package com.hugos.BanKING;

import com.hugos.BanKING.appuser.AppUser;
import com.hugos.BanKING.jwt.DecodedJwt;
import com.hugos.BanKING.role.Role;
import com.hugos.BanKING.jwt.jwtService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Component;

@SpringBootApplication
public class BanKINGApplication {
	public static void main(String[] args) {
		SpringApplication.run(BanKINGApplication.class, args);
	}

}

@Component
class ApplicationRunner implements CommandLineRunner {
	@Override
	public void run(String... args)  {
		AppUser hugo = new AppUser(
			null,
			"hugokorteapple@gmail.com",
			"123",
			Role.USER
		);
		String JWT = new jwtService().encode(hugo);
		System.out.println(JWT);
		DecodedJwt jwt = new jwtService().decode(JWT);
		System.out.println(jwt.getSubject());
	}
}
package ru.nik.alfafamily;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;

@SpringBootApplication
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
public class AlfaFamilyApplication {

	public static void main(String[] args) {
		SpringApplication.run(AlfaFamilyApplication.class, args);
	}

}

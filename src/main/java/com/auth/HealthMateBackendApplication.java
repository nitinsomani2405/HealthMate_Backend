package com.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(basePackages = "com")
@EntityScan(basePackages = {"com.auth.Models", "com.profile.Models","com.appointment.Models","com.prescription.Models","com.labreport.Models"})
@EnableJpaRepositories(basePackages = {"com.auth.Repository", "com.profile.Repository","com.appointment.Repository","com.prescription.Repository","com.labreport.Repository"})
public class HealthMateBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(HealthMateBackendApplication.class, args);
	}

}

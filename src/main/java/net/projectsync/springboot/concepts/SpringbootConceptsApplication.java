package net.projectsync.springboot.concepts;

import net.projectsync.springboot.concepts.service.PaymentService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class SpringbootConceptsApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringbootConceptsApplication.class, args);
	}

	/*
	@Bean
	CommandLineRunner run(PaymentService paymentService) {
		return args -> {
			paymentService.transfer("ACC1", "ACC2", 1000);
			paymentService.transfer("ACC3", "ACC4", 2000);
		};
	}
	*/
}

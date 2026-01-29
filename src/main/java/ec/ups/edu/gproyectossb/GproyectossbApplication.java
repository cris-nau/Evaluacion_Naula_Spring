package ec.ups.edu.gproyectossb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class GproyectossbApplication {

	public static void main(String[] args) {
		SpringApplication.run(GproyectossbApplication.class, args);
	}

}

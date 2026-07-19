package com.cuscatlan.coworking;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class CuscatlanApplication {

	public static void main(String[] args) {
		SpringApplication.run(CuscatlanApplication.class, args);
	}

}

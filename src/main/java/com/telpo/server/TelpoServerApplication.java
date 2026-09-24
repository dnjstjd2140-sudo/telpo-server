package com.telpo.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class TelpoServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(TelpoServerApplication.class, args);
	}

}

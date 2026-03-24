package com.volttrack.volttrack;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class VolttrackApplication {

	public static void main(String[] args) {
		SpringApplication.run(VolttrackApplication.class, args);
	}

}

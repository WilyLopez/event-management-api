package com.playzone.pems;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import jakarta.annotation.PostConstruct;
import java.util.TimeZone;

@SpringBootApplication
@EnableScheduling
public class PemsApplication {

	@PostConstruct
	public void init() {
		TimeZone.setDefault(TimeZone.getTimeZone("America/Lima"));
	}

	public static void main(String[] args) {
		TimeZone.setDefault(TimeZone.getTimeZone("America/Lima"));
		SpringApplication.run(PemsApplication.class, args);
	}

}

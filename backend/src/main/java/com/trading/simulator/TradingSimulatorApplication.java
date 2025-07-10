package com.trading.simulator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication // main spring boot application class
@RestController // handling http requests
public class TradingSimulatorApplication {

	public static void main(String[] args) {
		SpringApplication.run(TradingSimulatorApplication.class, args);
	}

	@GetMapping("/")
	public String home() {
		return "Trading Simulator API is running!";
	}

	@GetMapping("/health")
	public String health() {
		return "OK";
	}
}
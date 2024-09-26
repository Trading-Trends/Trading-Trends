package com.tradingtrends.gateway;

import com.tradingtrends.common.CommonApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class GatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(CommonApplication.class, args);
	}

}
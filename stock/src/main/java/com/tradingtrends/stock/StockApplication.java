package com.tradingtrends.stock;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class StockApplication {

	public static void main(String[] args) {

		// .env 파일 로드
		Dotenv dotenv = Dotenv.configure().filename(".env.dev").load();
		System.setProperty("APP_KEY", dotenv.get("APP_KEY"));
		System.setProperty("APP_SECRET", dotenv.get("APP_SECRET"));

		SpringApplication.run(StockApplication.class, args);
	}

}

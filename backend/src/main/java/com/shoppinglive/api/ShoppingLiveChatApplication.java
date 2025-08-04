package com.shoppinglive.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories
public class ShoppingLiveChatApplication {

	public static void main(String[] args) {
		SpringApplication.run(ShoppingLiveChatApplication.class, args);
	}

}

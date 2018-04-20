package com.avenuecode.imagestore;


import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.avenuecode.imagestore.model.Product;
import com.avenuecode.imagestore.services.ImageRepository;
import com.avenuecode.imagestore.services.ProductRepository;

@SpringBootApplication
@EnableTransactionManagement
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	/*
	 * creates the root product
	 */
	@Bean
	CommandLineRunner init(ProductRepository productRepository,
			ImageRepository imageRepository) {
		return (evt) -> {		
			productRepository.save(new Product (null, "rootProduct"));
		};
	}

}

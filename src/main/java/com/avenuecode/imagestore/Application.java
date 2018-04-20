package com.avenuecode.imagestore;


import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.avenuecode.imagestore.entities.Product;
import com.avenuecode.imagestore.entities.ProductRepository;
import com.avenuecode.imagestore.entities.Image;
import com.avenuecode.imagestore.entities.ImageRepository;

import java.util.Arrays;

@SpringBootApplication
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

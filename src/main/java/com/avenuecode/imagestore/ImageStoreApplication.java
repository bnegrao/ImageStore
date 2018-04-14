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
public class ImageStoreApplication {

	public static void main(String[] args) {
		SpringApplication.run(ImageStoreApplication.class, args);
	}

	@Bean
	CommandLineRunner init(ProductRepository productRepository,
			ImageRepository imageRepository) {
		return (evt) -> Arrays.asList(
				"jhoeller,bnegrao,dsyer,pwebb,ogierke,rwinch,mfisher,mpollack,jlong".split(","))
				.forEach(
						a -> {
							Product product = productRepository.save(new Product(a,
									"password"));
							imageRepository.save(new Image(product,
									"http://bookmark.com/1/" + a, "A description"));
							imageRepository.save(new Image(product,
									"http://bookmark.com/2/" + a, "A description"));
						});
	}

}

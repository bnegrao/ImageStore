package com.avenuecode.imagestore;

import java.net.URI;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.avenuecode.imagestore.entities.ProductRepository;
import com.avenuecode.imagestore.entities.Image;
import com.avenuecode.imagestore.entities.ImageRepository;

/*
 * Endpoints:
 * POST/PUT /product/{productID}/product - add/update a product as a child of a parent product identified by {productID}. 
 * The special, '-1' id, must be used as the parendID for the root product.
 * POST/PUT /product/{productID}/image - add/update an image, as a child of a parent product. * 
 * GET /product/{productID} - retrieves a product by id
 */

@RestController
@RequestMapping("/{productId}/images")
class ImageStoreRestController {

	private final ImageRepository imageRepository;

	private final ProductRepository productRepository;

	@Autowired
	ImageStoreRestController(ImageRepository imageRepository,
						   ProductRepository productRepository, MappingJackson2HttpMessageConverter converter) {
		this.imageRepository = imageRepository;
		this.productRepository = productRepository;

	}

	@RequestMapping(method = RequestMethod.GET)
	Collection<Image> readBookmarks(@PathVariable String userId) {
		this.validateProduct(userId);
		return this.imageRepository.findByProductName(userId);
	}

	@RequestMapping(method = RequestMethod.POST)
	ResponseEntity<?> add(@PathVariable String userId, @RequestBody Image input) {
		this.validateProduct(userId);
		
		System.out.println(input);

		return this.productRepository
				.findById(userId)
				.map(account -> {
					Image result = imageRepository.save(new Image(account,
							input.getUri(), input.getDescription()));

					URI location = ServletUriComponentsBuilder
						.fromCurrentRequest().path("/{id}")
						.buildAndExpand(result.getId()).toUri();

					return ResponseEntity.created(location).build();
				})
				.orElse(ResponseEntity.noContent().build());

	}

	@RequestMapping(method = RequestMethod.GET, value = "/{imageId}")
	Image readBookmark(@PathVariable String productId, @PathVariable Long imageId) {
		this.validateProduct(productId);
		return this.imageRepository.findById(imageId).get();
	}

	private void validateProduct(String productId) {
		this.productRepository.findById(productId).orElseThrow(
				() -> new ProductNotFoundException(productId));
	}
}
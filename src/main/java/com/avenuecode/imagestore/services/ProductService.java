package com.avenuecode.imagestore.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.avenuecode.imagestore.model.Product;

@Component
public class ProductService {
	
	@Autowired
	private ProductRepository productRepository;
	
	/**
	 * Makes lazyload of child products and/or images of a given parent product identified by {productId}
	 * @param productId parent product id
	 * @param products if true it enables lazyload of child products
	 * @param images if true it enables lazyload of child images
	 * @return the product object after lazyload of sub-components or null if {productId} was not found on the database.
	 */
	@Transactional
	public Product getProductWithRelationships(Long productId, boolean products, boolean images) {
		Product product = productRepository.findById(productId).orElse(null);
		
		if (product == null) { return null; }
		
		if (products) {
			// make lazy load of child products
			product.getProducts().size();
		}
		
		if (images) {
			// make lazy load of child images
			product.getImages().size();
		}
		
		
		return product;
	}
	
	@Transactional
	public List<Product> getAllProductsWithRelationships(boolean products, boolean images) {
		List<Product> allProds = productRepository.findAll();
		
		for (Product product: allProds) {
			if (products) {
				// make lazy load of child products
				product.getProducts().size();
			}
			
			if (images) {
				// make lazy load of child images
				product.getImages().size();
			}			
		}
		
		return allProds;
	}

}

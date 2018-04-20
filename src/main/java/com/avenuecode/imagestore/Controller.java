package com.avenuecode.imagestore;

import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.avenuecode.imagestore.model.Image;
import com.avenuecode.imagestore.model.Product;
import com.avenuecode.imagestore.services.ImageRepository;
import com.avenuecode.imagestore.services.ProductRepository;
import com.avenuecode.imagestore.services.ProductService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;


@RestController
@RequestMapping("/")
class Controller {

	private final ImageRepository imageRepository;

	private final ProductRepository productRepository;
	
	private final ProductService productService;

	@Autowired
	Controller(ImageRepository imageRepository,
						   ProductRepository productRepository,
							ProductService productService) {
		this.imageRepository = imageRepository;
		this.productRepository = productRepository;
		this.productService = productService;

	}


	@RequestMapping(method = RequestMethod.POST, value = "products/{parentId}/image")
	ResponseEntity<?> addImage(@PathVariable Long parentId, @RequestBody Image input) {		
		System.out.println(input);

		return this.productRepository
				.findById(parentId)
				.map(parentProduct -> {
					Image result = imageRepository.save(new Image(parentProduct, input.getDescription()));

					URI location = ServletUriComponentsBuilder
						.fromCurrentRequest().replacePath("/images/{id}")
						.buildAndExpand(result.getId()).toUri();

					return ResponseEntity.created(location).build();
				})
				.orElseThrow(() -> new ProductNotFoundException(parentId));
	}
	
	@RequestMapping(method = RequestMethod.POST, value = "products/{parentId}")
	ResponseEntity<?> addProduct(@PathVariable Long parentId, @RequestBody Product input) {		

		return this.productRepository
				.findById(parentId)
				.map(parentProduct -> {
					Product result = productRepository.save(new Product(parentProduct.getId(),input.getName()));

					URI location = ServletUriComponentsBuilder
						.fromCurrentRequest().replacePath("/products/{id}")
						.buildAndExpand(result.getId()).toUri();

					return ResponseEntity.created(location).build();
				})
				.orElseThrow(() -> new ParentNotFoundException(parentId));
	}	
	
	@RequestMapping(method = RequestMethod.PUT, value = "/products/{productId}")
	ResponseEntity<?> updateProduct(@PathVariable Long productId, @RequestBody Product input) {
		
		Product product = productRepository.findById(productId).orElseThrow(() -> new ProductNotFoundException(productId));
		
		product.setName(input.getName());
		
		productRepository.save(product);	
		
		return ResponseEntity.ok().build();
	}	

	@RequestMapping(method = RequestMethod.PUT, value = "/images/{imageId}")
	ResponseEntity<?> updateImage(@PathVariable Long imageId, @RequestBody Image input) {
		
		Image image = imageRepository.findById(imageId).orElseThrow(() -> new ImageNotFoundException(imageId));
		
		image.setDescription(input.getDescription());
		
		imageRepository.save(image);	
		
		return ResponseEntity.ok().build();
	}	

	@RequestMapping(method = RequestMethod.GET, value = "images/{imageId}")
	Image getImage(@PathVariable Long imageId) {
		return this.imageRepository.findById(imageId).orElseThrow(() -> new ImageNotFoundException(imageId));
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "images")
	List<Image> getAllImages() {
		return this.imageRepository.findAll();
	}	
	
//	@RequestMapping(method = RequestMethod.GET, value = "products/{productId}", produces="application/json;charset=UTF-8")
//	@ResponseBody
//	String getProduct(@PathVariable Long productId, @RequestParam(value="includeRelationship", required=false) String includeRelationship) throws JsonProcessingException, ProductNotFoundException {			
//		return writeJson (includeRelationship, this.productRepository.findById(productId).orElseThrow(() -> new ProductNotFoundException(productId)));				
//	}
	
	@RequestMapping(method = RequestMethod.GET, value = "products/{productId}", produces="application/json;charset=UTF-8")
	@ResponseBody
	String getProduct(@PathVariable Long productId, @RequestParam(value="includeRelationship", required=false) String includeRelationship) throws JsonProcessingException, ProductNotFoundException {			
		boolean loadChildImages = false;
		boolean loadChildProducts = false;
		if (includeRelationship != null) {
			if (includeRelationship.contains("product")) {
				loadChildProducts = true;
			}
			if (includeRelationship.contains("image")) {
				loadChildImages = true;
			}
		}	
		
		
		Product product = this.productService.getProductWithRelationships(productId, loadChildProducts, loadChildImages);
		
		return writeJson(product, loadChildProducts, loadChildImages);			
	}	
	
	
	/**
	 * 
	 * @param includeRelationship load child relationships. Ex: /products?includeRelationship=product,image
	 * @return
	 * @throws JsonProcessingException 
	 */
	@RequestMapping(method = RequestMethod.GET, value = "products", produces="application/json;charset=UTF-8")
	@ResponseBody
	String getAllProducts(@RequestParam(value="includeRelationship", required=false) String includeRelationship) throws JsonProcessingException {	
		boolean loadChildImages = false;
		boolean loadChildProducts = false;
		if (includeRelationship != null) {
			if (includeRelationship.contains("product")) {
				loadChildProducts = true;
			}
			if (includeRelationship.contains("image")) {
				loadChildImages = true;
			}
		}	
		
		List<Product> allProds = this.productService.getAllProductsWithRelationships(loadChildProducts, loadChildImages);
		return writeJson (allProds, loadChildProducts, loadChildImages);
	}	
	
	private String writeJson(Object value, boolean loadChildProducts, boolean loadChildImages) throws JsonProcessingException {	
		
		ObjectMapper objectMapper = new ObjectMapper();
		ObjectWriter objectWriter = objectMapper.writerWithView(Views.chooseViewFor(loadChildProducts, loadChildImages)); 
		
		return objectWriter.writeValueAsString(value);	
	}	
	
	@RequestMapping(method = RequestMethod.DELETE, value = "/products/{productId}")
	ResponseEntity<?> deleteProduct(@PathVariable Long productId) {		
		Product product = productRepository.findById(productId).orElseThrow(() -> new ProductNotFoundException(productId));		
		productRepository.delete(product);		
		return ResponseEntity.ok().build();
	}		

}
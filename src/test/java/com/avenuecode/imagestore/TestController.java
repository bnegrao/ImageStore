package com.avenuecode.imagestore;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.http.MockHttpOutputMessage;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.context.WebApplicationContext;

import com.avenuecode.imagestore.model.Image;
import com.avenuecode.imagestore.model.Product;
import com.avenuecode.imagestore.services.ImageRepository;
import com.avenuecode.imagestore.services.ProductRepository;
import com.avenuecode.imagestore.services.ProductService;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@EnableTransactionManagement
public class TestController {


    private MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(),
            Charset.forName("utf8"));

    private MockMvc mockMvc;

    @SuppressWarnings("rawtypes")
	private HttpMessageConverter mappingJackson2HttpMessageConverter;

    private Product rootProduct;
	private Image rootImage;

    private List<Product> productList = new ArrayList<Product>();

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private ProductService productService;

	private Product childProduct1;

	private Image childImage1;

	private Product childProduct2;

    @Autowired
    void setConverters(HttpMessageConverter<?>[] converters) {

        this.mappingJackson2HttpMessageConverter = Arrays.asList(converters).stream()
            .filter(hmc -> hmc instanceof MappingJackson2HttpMessageConverter)
            .findAny()
            .orElse(null);

        assertNotNull("the JSON message converter must not be null",
                this.mappingJackson2HttpMessageConverter);
    }

    @Before
    public void setup() throws Exception {
        this.mockMvc = webAppContextSetup(webApplicationContext).build();
        populateTheDb();
    }
    
    private void populateTheDb() {
        this.imageRepository.deleteAllInBatch();
        this.productRepository.deleteAllInBatch();

        this.rootProduct = productRepository.save(new Product(null, "rootProduct")); 
        this.rootImage = imageRepository.save(new Image(rootProduct, "rootImage"));
        this.childProduct1 = productRepository.save(new Product (rootProduct.getId(), "childProduct1"));
        this.childProduct2 = productRepository.save(new Product (rootProduct.getId(), "childProduct2"));
        this.childImage1 = imageRepository.save(new Image (childProduct1, "childImage1"));
        
        this.productList = productRepository.findAll();    	
    }
    
    
    /**
     * retrieving all products with relationships
     */
    @Test
    public void getAllProductsWithRelationships() throws Exception {
    	mockMvc.perform(get("/products" + "?includeRelationship=product,image"))
    	.andExpect(status().isOk())
    	.andExpect(content().contentType(contentType))
    	.andExpect(jsonPath("$[0].childProductIds", hasSize(2)))
    	.andExpect(jsonPath("$[0].childImageIds",hasSize(1)))
    	.andExpect(jsonPath("$[1].childProductIds", hasSize(0)))
    	.andExpect(jsonPath("$[1].childImageIds",hasSize(1)))
    	.andExpect(jsonPath("$[2].childProductIds", hasSize(0)))
    	.andExpect(jsonPath("$[2].childImageIds",hasSize(0)));  	    			
    }    

    
    @Test
    public void getAllImages() throws Exception {
    	mockMvc.perform(get("/images"))
    	.andExpect(status().isOk())
    	.andExpect(content().contentType(contentType))
    	.andExpect(jsonPath("$", hasSize(2)))
    	.andExpect(jsonPath("$[0].id", is(rootImage.getId().intValue())))
    	.andExpect(jsonPath("$[1].id", is(childImage1.getId().intValue())));
    }
    
    
    @Test
    public void getImage() throws Exception {
    	getImage(rootImage);
    }

	private void getImage(Image image) throws Exception {
		mockMvc.perform(get("/images/" + image.getId()))
    		.andExpect(status().isOk())
    		.andExpect(content().contentType(contentType))
    		.andExpect(jsonPath("$.id", is(image.getId().intValue())))
    		.andExpect(jsonPath("$.description", is(image.getDescription())));
	}
    
    @Test
    public void getAllProducts() throws Exception {
    	mockMvc.perform(get("/products"))
    	.andExpect(status().isOk())
    	.andExpect(content().contentType(contentType))
        .andExpect(jsonPath("$", hasSize(3)))
        .andExpect(jsonPath("$[0].id", is(this.productList.get(0).getId().intValue())))
        .andExpect(jsonPath("$[1].id", is(this.productList.get(1).getId().intValue())))
        .andExpect(jsonPath("$[2].id", is(this.productList.get(2).getId().intValue())));
    }
    

    
    
    @Test
    public void getProduct() throws Exception {
    		getProduct(rootProduct);
    }
    
    private void getProduct(Product product) throws Exception {    	    	    	
        mockMvc.perform(get("/products/" + product.getId()))
        .andExpect(status().isOk())
        .andExpect(content().contentType(contentType))
        .andExpect(jsonPath("$.id", is(product.getId().intValue())))
        .andExpect(jsonPath("$.parentId", is(product.getParentId() == null ? null : product.getParentId().intValue())))
        .andExpect(jsonPath("$.name", is(product.getName())));
    }
    
    /**
     * Test loading of a product and its child sub-components
     * @throws Exception
     */
    @Test
    public void getProductWithRelationship() throws Exception {
    	mockMvc.perform(get("/products/" + rootProduct.getId() + "?includeRelationship=product,image"))
    	.andExpect(status().isOk())
    	.andExpect(content().contentType(contentType))
    	.andExpect(jsonPath("$.childProductIds").isArray())
    	.andExpect(jsonPath("$.childProductIds", hasSize(2)))
    	.andExpect(jsonPath("$.childImageIds").isArray())
    	.andExpect(jsonPath("$.childImageIds", hasSize(1)));    	
    }    

    /**
     * Adds a new Product then checks if the product can be retrieved
     * from the URI found in the Location header sent as reponse to the POST request.
     * @throws Exception
     */
    @Test
    public void postProduct() throws Exception {
        String productJson = json(new Product(rootProduct.getId(), "dummy-1"));

        String newProductUrl = this.mockMvc.perform(
        		post("/products" + "/" + rootProduct.getId())
                .contentType(contentType)
                .content(productJson))
        	.andExpect(status().isCreated())    		
        	.andReturn().getResponse().getHeader("Location");	
        	
        	// check if the new image can be retrieved
        	this.mockMvc.perform(get(newProductUrl))
        	.andExpect(status().isOk());
    }
    
    @Test
    public void postProductInvalidParent() throws Exception {
    	Long invalidId = 9999L;
    	String productJson = json(new Product (invalidId, "invalid parent"));
    	
        this.mockMvc.perform(
        		post("/products" + "/" + invalidId)
                .contentType(contentType)
                .content(productJson))
        	.andExpect(status().isBadRequest());    	
    }
    
    /**
     * Adds a new Image object then checks if the image can be retrieved
     * from the URI found in the Location header sent as reponse to the POST request.
     * @throws Exception
     */
    @Test
    public void postImage() throws Exception {
    	String imageJson = json(new Image(rootProduct, "img-1"));
    	
    	String newImageUrl = this.mockMvc.perform(
    			post("/products/" + rootProduct.getId() + "/image")
    			.contentType(contentType)
    			.content(imageJson))
    		.andExpect(status().isCreated())
    		.andReturn().getResponse().getHeader("Location");	
    	
    	// check if the new image can be retrieved
    	this.mockMvc.perform(get(newImageUrl))
    	.andExpect(status().isOk());
    	
    }
    
    /**
     * Tries to post a new image under a parent product that does not exist.
     * Must return 400 Bad Request
     * @throws Exception
     */
    @Test
    public void postImageWrongParentId() throws Exception {
    	String imageJson = json(this.childImage1);
    	
    	Long wrongParentId = 9999L;
    	
    	this.mockMvc.perform(
    			post("/products/" + wrongParentId + "/image")
    			.contentType(contentType)
    			.content(imageJson))
    		.andExpect(status().isBadRequest()); 	
    }
    
    /**
     * changes the description of an existing image, save in on the db, then retrieve it from the db
     * to verify that the new description was really saved.
     * @throws Exception
     */
    @Test
    public void updateImage() throws Exception {
    	// changing the name of an existing product
    	childImage1.setDescription("trickyThing");
    	
    	String imageJson = json(childImage1);
    	
    	this.mockMvc.perform(
    			put("/images/" + childImage1.getId())
    			.contentType(contentType)
    			.content(imageJson)
    			).andExpect(status().isOk());
    	
    	// retrieve the product from the database and certify that it was updated.
    	getImage(childImage1);
    }    
    
    
    /**
     * changes the name of an existing product, save in on the db, then retrieve it from the db
     * to verify that the new name was really saved.
     * @throws Exception
     */
    @Test
    public void updateProduct() throws Exception {
    	// changing the name of an existing product
    	childProduct1.setName("trickyThing");
    	
    	String productJson = json(childProduct1);
    	
    	this.mockMvc.perform(
    			put("/products/" + childProduct1.getId())
    			.contentType(contentType)
    			.content(productJson)
    			).andExpect(status().isOk());
    	
    	// retrieve the product from the database and certify that it was updated.
    	getProduct(childProduct1);
    }
    
    /**
     * tries to update an inexistent product. must return 400 bad request
     * @throws Exception
     */
    @Test
    public void updateProductWrongId() throws Exception { 	
    	Long wrongId = 9999L;
    	
    	String productJson = json (new Product (wrongId, "inexistent product"));
    	
    	this.mockMvc.perform(
    			put("/products/" + wrongId)
    			.contentType(contentType)
    			.content(productJson)
    			).andExpect(status().isBadRequest());
    }
    
    /**
     * Deletes the whole tree of products and images. Test if the tree is empty after the deletion.
     * @throws Exception
     */
    @Test
    public void deleteRootProduct() throws Exception {
    	// this will delete everything in the db
    	this.mockMvc.perform(delete("/products/" + rootProduct.getId()))
    		.andExpect(status().isOk());
    	
    	// check if there are no more products
    	this.mockMvc.perform(get ("/products"))
    	.andExpect(status().isOk())
    	.andExpect(content().contentType(contentType))
    	.andExpect(jsonPath("$", hasSize(0)));
    	
    	// check if there are no more images
    	this.mockMvc.perform(get ("/images"))
    	.andExpect(status().isOk())
    	.andExpect(content().contentType(contentType))
    	.andExpect(jsonPath("$", hasSize(0)));
    	
    	// recreate the data
    	populateTheDb();
    }
    
    @Test
    public void deleteProductWrongId() throws Exception {
    	Long wrongId = 9999L;
    	
    	this.mockMvc.perform(delete("/products/" + wrongId))
		.andExpect(status().isBadRequest());
    }

    @SuppressWarnings("unchecked")
	protected String json(Object o) throws IOException {
        MockHttpOutputMessage mockHttpOutputMessage = new MockHttpOutputMessage();
        this.mappingJackson2HttpMessageConverter.write(
                o, MediaType.APPLICATION_JSON, mockHttpOutputMessage);
        return mockHttpOutputMessage.getBodyAsString();
    }
}
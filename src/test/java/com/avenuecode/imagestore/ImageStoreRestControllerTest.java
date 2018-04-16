package com.avenuecode.imagestore;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.http.MockHttpOutputMessage;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import com.avenuecode.imagestore.ImageStoreApplication;
import com.avenuecode.imagestore.entities.Product;
import com.avenuecode.imagestore.entities.ProductRepository;
import com.avenuecode.imagestore.entities.Image;
import com.avenuecode.imagestore.entities.ImageRepository;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.*;

/**
 * @author Josh Long
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ImageStoreApplication.class)
@WebAppConfiguration
public class ImageStoreRestControllerTest {


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

        this.imageRepository.deleteAllInBatch();
        this.productRepository.deleteAllInBatch();

        this.rootProduct = productRepository.save(new Product(null, "rootProduct")); 
        this.rootImage = imageRepository.save(new Image(rootProduct, "rootImage"));
        this.childProduct1 = productRepository.save(new Product (rootProduct.getId(), "childProduct1"));
        this.childProduct2 = productRepository.save(new Product (rootProduct.getId(), "childProduct2"));
        this.childImage1 = imageRepository.save(new Image (childProduct1, "childImage1"));
        
        this.productList = productRepository.findAll();
    }

    @Test
    public void productNotFound() throws Exception {
        mockMvc.perform(post("/1/product/")
                .content(this.json(new Image(null, null)))
                .contentType(contentType))
                .andExpect(status().isNotFound());
    }

    @Test
    public void getProduct() throws Exception {
    		getProduct(rootProduct);
    }
    
    private void getProduct(Product product) throws Exception {    	    	    	
        mockMvc.perform(get("/product/" + product.getId()))
        .andExpect(status().isOk())
        .andExpect(content().contentType(contentType))
        .andExpect(jsonPath("$.id", is(product.getId().intValue())))
        .andExpect(jsonPath("$.parentId", is(product.getParentId() == null ? null : product.getParentId().intValue())))
        .andExpect(jsonPath("$.name", is(product.getName())));
    }
    
    @Test
    public void getImage() throws Exception {
    	mockMvc.perform(get("/image/" + rootImage.getId()))
    		.andExpect(status().isOk())
    		.andExpect(content().contentType(contentType))
    		.andExpect(jsonPath("$.id", is(rootImage.getId().intValue())))
    		.andExpect(jsonPath("$.description", is(rootImage.getDescription())));
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

//    @Test
//    public void readBookmarks() throws Exception {
//        mockMvc.perform(get("/" + userName + "/bookmarks"))
//                .andExpect(status().isOk())
//                .andExpect(content().contentType(contentType))
//                .andExpect(jsonPath("$", hasSize(2)))
//                .andExpect(jsonPath("$[0].id", is(this.imageList.get(0).getId().intValue())))
//                .andExpect(jsonPath("$[0].uri", is("http://bookmark.com/1/" + userName)))
//                .andExpect(jsonPath("$[0].description", is("A description")))
//                .andExpect(jsonPath("$[1].id", is(this.imageList.get(1).getId().intValue())))
//                .andExpect(jsonPath("$[1].uri", is("http://bookmark.com/2/" + userName)))
//                .andExpect(jsonPath("$[1].description", is("A description")));
//    }

    @Test
    public void addChildProduct() throws Exception {
        String productJson = json(new Product(rootProduct.getId(), "dummy-1"));

        this.mockMvc.perform(
        		post("/" + rootProduct.getId() + "/product")
                .contentType(contentType)
                .content(productJson))
        	.andExpect(status().isCreated());
    }
    
    @Test
    public void addChildImage() throws Exception {
    	String imageJson = json(new Image(rootProduct, "img-1"));
    	
    	this.mockMvc.perform(
    			post("/" + rootProduct.getId() + "/image")
    			.contentType(contentType)
    			.content(imageJson))
    		.andExpect(status().isCreated());    			
    }
    
    /**
     * changes the name of an existing product, save in on the db, then retrieve it from the db
     * to verify that the new name was really saved.
     * @throws Exception
     */
    @Test
    public void updateNameInExistingProduct() throws Exception {
    	// changing the name
    	childProduct1.setName("trickyThing");
    	
    	String productJson = json(childProduct1);
    	
    	this.mockMvc.perform(
    			put("/product/" + childProduct1.getId())
    			.contentType(contentType)
    			.content(productJson)
    			).andExpect(status().isOk());
    	
    	// retrieve the product from the database and certify that it was updated.
    	getProduct(childProduct1);
    }
    
    /**
     * Make childProduct2 be son of childProduct1 instead of the rootProduct.
     * @throws Exception
     */
    @Test
    public void updateParentIdInExistingProduct() throws Exception {    	
    	// changing the parentId
    	childProduct2.setParentId(childProduct1.getId());
    	
    	String productJson = json(childProduct2);
    	
    	this.mockMvc.perform(
    			put("/product/" + childProduct2.getId())
    			.contentType(contentType)
    			.content(productJson)
    			).andExpect(status().isOk());
    	
    	// retrieve the product from the database and certify that it was updated.
    	getProduct(childProduct2);    	
    	
    }
    
    /**
     * Tries to change the parentId of an existing product to an inexistent product. Should return 400 Bad Request
     * @throws Exception
     */
    @Test
    public void updateProductToUnexistentParentId() throws Exception {
    	// changing the parentId to an inexistent id
    	childProduct2.setParentId(999L);    	

    	String productJson = json(childProduct2);
    	
    	this.mockMvc.perform(
    			put("/product/" + childProduct2.getId())
    			.contentType(contentType)
    			.content(productJson)
    			).andExpect(status().isBadRequest());
    }
    

    @SuppressWarnings("unchecked")
	protected String json(Object o) throws IOException {
        MockHttpOutputMessage mockHttpOutputMessage = new MockHttpOutputMessage();
        this.mappingJackson2HttpMessageConverter.write(
                o, MediaType.APPLICATION_JSON, mockHttpOutputMessage);
        return mockHttpOutputMessage.getBodyAsString();
    }
}
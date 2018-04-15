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

    private String userName = "bdussault";

    @SuppressWarnings("rawtypes")
	private HttpMessageConverter mappingJackson2HttpMessageConverter;

    private Product rootProduct;
	private Image rootImage;

    private List<Image> imageList = new ArrayList<>();

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ProductRepository productRepository;

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
        mockMvc.perform(get("/product/" + rootProduct.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.id", is(rootProduct.getId().intValue())))
                .andExpect(jsonPath("$.name", is(rootProduct.getName())));
    }
    
    @Test
    public void getImage() throws Exception {
    	mockMvc.perform(get("/image/" + rootImage.getId()))
    		.andExpect(status().isOk())
    		.andExpect(content().contentType(contentType))
    		.andExpect(jsonPath("$.id", is(rootImage.getId().intValue())))
    		.andExpect(jsonPath("$.description", is(rootImage.getDescription())));
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
        String productJson = json(new Product(rootProduct, "dummy-1"));

        this.mockMvc.perform(
        		post("/" + rootProduct.getId() + "/product")
                .contentType(contentType)
                .content(productJson))
        	.andExpect(status().isCreated());
    }
    
    public void addChildImage() throws Exception {
    	String imageJson = json(new Image(rootProduct, "img-1"));
    	
    	this.mockMvc.perform(
    			post("/" + rootProduct.getId() + "/image")
    			.contentType(contentType)
    			.content(imageJson))
    		.andExpect(status().isCreated());    			
    }

    @SuppressWarnings("unchecked")
	protected String json(Object o) throws IOException {
        MockHttpOutputMessage mockHttpOutputMessage = new MockHttpOutputMessage();
        this.mappingJackson2HttpMessageConverter.write(
                o, MediaType.APPLICATION_JSON, mockHttpOutputMessage);
        return mockHttpOutputMessage.getBodyAsString();
    }
}
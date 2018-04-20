# ImageStore
Conceptual REST application in Java Spring to store a hirerachy of "Image Products"

### How to execute the app  

Once you downloaded the application source to a local folder, cd to the root folder where the pom.xml file is and run the following commands:

**compile the app:** `mvn compile`  
**run the tests:** `mvn test`  
**run the app:** `mvn spring-boot:run`

### API Endpoints

**GET /products**  
**Description:** retrieves all products. By default, it won't retrieve child products or images.  
**Url Params:** Optional: `includeRelationship=[image[,product]]`. If present the API to show the sets of child images and/or products.  Ex: `GET /products?includeRelationship=image,product`  
**Response Codes:** 200 - Success. 200 - In case there are no products at all it's returned an json with an empty list.  
**Response Data:**
- Example without relationships: `[{"id":1,"name":"rootProduct","parentId":null},{"id":7,"name":"product","parentId":1}]`
- Example with relationships: `[{"id":1,"name":"rootProduct","parentId":null,"childImageIds":[11,4,12,5],"childProductIds":[7]},{"id":7,"name":"product","parentId":1,"childImageIds":[],"childProductIds":[]}]` 


**GET /products/{productId}**  
**Description:** retrieves a product identified by {productId}  
**Url Params:** Optional: `includeRelationship=[image[,product]]`. If present the API to show the sets of child images and/or products.  Ex: `GET /products/1?includeRelationship=product,image`  
**Response Codes:** 200 - Success. 400 - Product Not Found.  
**Response Data:** Ex: `{"id":1,"name":"rootProduct","parentId":null,"childImageIds":[2],"childProductIds":[3,4]}`

**POST /products/{productId}**  
**Description:** adds a new product as child of the product identified by {productId}  
**Data params**: Ex: `{  "name": "newProductName" } `   
**Response Codes:** 201 - Created. 400 - The parent product {productId} was not found in the system.   
**Response Headers:** the __Location__ response header shows the URL to access the created object.   

**PUT /products/{productId}**   
**Description:** updates a product identified by {productId}. Only the 'name' property can be updated, any other property will be ignored.  
**Data params**: Ex: `{  "name": "otherProductName" } `   
**Response Codes:** 200 - Success. 400 - Product Not Found. 

**DELETE /products/{productId}**   
**Description:** deletes a product identified by {productId}, cascading the deletion to all its descending objects recursively.  
**Response Codes:** 200 - Success. 400 - Product Not Found.

**GET /images**    
**Description:** retrieves all images.  
**Response Codes:** 200 - Success. 200 - In case there are no images at all it's returned an json with an empty list.

**GET /images/{imageId}**    
**Description:** retrieves an image by its id  
**Response Codes:** 200 - Success. 400 - Image Not Found.  
**Response Data:** `{
"id": 3,
"description": "image1",
"parentId": 1
}`

**POST /products/{productId}/image**  
**Description:** adds an image as child of the product identified by {productId}  
**Data params**: Ex: `{  "description": "imageName" } `   
**Response Codes:** 201 - Created. 400 - The parent product {productId} was not found in the system.   
**Response Headers:** the __Location__ response header shows the URL to access the object created.

**PUT /images/{imageId}**   
**Description:** updates an image. Only the 'name' property can be updated, any other property will be ignored.     
**Data params**: Ex: `{  "description": "otherImageName" } `   
**Response Codes:** 200 - Success. 400 - Image Not Found.  

**DELETE /images/{imageId}**  
**Description:** deletes an image identified by {imageId}  
**Response Codes:** 200 - Success. 400 - Image Not Found.




### Scenario

We have a Product Entity with One to Many relationship with Image entity

Product also has a Many to One relationship with itself (Many Products to one Parent Product) 

1- Build a Restful service using JAX-RS to perform CRUD operations on a Product resource using Image as a sub-resource of Product.

2- Your API classes should perform these operations:

1. Create, update and delete products
2. Create, update and delete images
3. Get all products excluding relationships (child products, images) 
4. Get all products including specified relationships (child product and/or images) 
5. Same as 3 using specific product identity 
6. Same as 4 using specific product identity 
7. Get set of child products for specific product 
8. Get set of images for specific product

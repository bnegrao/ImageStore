# ImageStore
Conceptual REST application in Java Spring to store a hirerachy of "Image Products"

Scenario:

We have a Product Entity with One to Many relationship with Image entity

Product also has a Many to One relationship with itself (Many Products to one Parent Product) 

1º Build a Restful service using JAX-RS to perform CRUD operations on a Product resource using Image as a sub-resource of Product.

2º Your API classes should perform these operations:

1) Create, update and delete products
2) Create, update and delete images
3) Get all products excluding relationships (child products, images) 
4) Get all products including specified relationships (child product and/or images) 
5) Same as 3 using specific product identity 
6) Same as 4 using specific product identity 
7) Get set of child products for specific product 
8) Get set of images for specific product
package com.avenuecode.imagestore.entities;


import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

@Entity
public class Product {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    @OneToMany(mappedBy = "product")
    private Set<Image> images = new HashSet<Image>();
    
    @OneToMany()
    private Set<Product> products = new HashSet<Product>();
    
    @ManyToOne(optional = true, cascade={CascadeType.ALL})	
    @JoinColumn(name="parent_id")
    private Product parentProduct;
    
    @SuppressWarnings("unused")
	private Product() { } // JPA only

    /**
     * 
     * @param parentProduct Reference to the parent of this product. If parentProduct is null, that means this is the "rootProduct"
     * @param name name of the product.
     */
    public Product(Product parentProduct, String name) {
    	this.parentProduct = parentProduct;
    	this.name = name;
	}
    
    /**
     * Returns a clone of the origin object
     * @param origin
     */
    public Product(Product origin) {
    	this.id = origin.id;
    	this.images = new HashSet<Image>(origin.images);
    	this.name = origin.name;
    	this.parentProduct = origin.parentProduct;
    	this.products = new HashSet<Product>(origin.products);
    }

	public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Set<Image> getImages() {
        return images;
    }

	public Product getParentProduct() {
		return parentProduct;
	}

	public void setParentProduct(Product parentProduct) {
		this.parentProduct = parentProduct;
	}
	
	public Set<Product> getProducts() {
		return this.products;
	}

	public void setName(String newName) {
		this.name = newName;		
	}
}
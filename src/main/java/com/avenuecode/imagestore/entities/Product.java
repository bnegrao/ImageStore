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
    
    private Long parentId;
    
    @SuppressWarnings("unused")
	private Product() { } // JPA only

    /**
     * 
     * @param The id of the parent product. If parentId is null, that means this is the "rootProduct"
     * @param name name of the product.
     */
    public Product(Long parentId, String name) {
    	this.parentId = parentId;
    	this.name = name;
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

	public Long getParentId() {
		return parentId;
	}

	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}
	
	public Set<Product> getProducts() {
		return this.products;
	}

	public void setName(String newName) {
		this.name = newName;		
	}
}
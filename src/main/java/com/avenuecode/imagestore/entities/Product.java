package com.avenuecode.imagestore.entities;


import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import com.avenuecode.imagestore.Views;
import com.fasterxml.jackson.annotation.JsonView;

@Entity
public class Product {

    @JsonView(Views.Basic.class)
    @Id
    @GeneratedValue
    @Column(name="id")
    private Long id;

    @JsonView(Views.Basic.class)
    private String name;

    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonView(Views.Images.class)
    private Set<Image> images = new HashSet<Image>();
    
    @OneToMany(mappedBy = "parentId", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonView(Views.Products.class)
    private Set<Product> products = new HashSet<Product>();
    
    @JsonView(Views.Basic.class)
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
package com.avenuecode.imagestore.model;


import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import com.avenuecode.imagestore.Views;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
    @JsonIgnore
    private Set<Image> images = new HashSet<Image>();
    
    @OneToMany(mappedBy = "parentId", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonIgnore
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
	
	@JsonView(Views.Images.class)
	public Long[] getChildImageIds () {
		Long [] ids = new Long[this.images.size()];
		int i = 0;
		for (Image image: this.images) {
			ids[i++] = image.getId();
		}
		
		return ids;
	}
	
	@JsonView(Views.Products.class)
	public Long[] getChildProductIds () {
		Long [] ids = new Long[this.products.size()];
		int i = 0;
		for (Product product: this.products) {
			ids[i++] = product.getId();
		}
		return ids;
	}	
}
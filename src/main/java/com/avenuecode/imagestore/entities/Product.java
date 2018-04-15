package com.avenuecode.imagestore.entities;


import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;

@Entity
public class Product {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    @OneToMany(mappedBy = "product")
    private Set<Image> images = new HashSet<>();
    
    @ManyToOne(optional = true)	
    @JoinColumn(name="parent_id")
    @NotNull
    private Product parentProduct;
    
    @SuppressWarnings("unused")
	private Product() { } // JPA only

    public Product(Product parentProduct, String name) {
    	this.parentProduct = parentProduct;
    	this.name = name;
	}

	public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Set<Image> getBookmarks() {
        return images;
    }

	public Product getParentProduct() {
		return parentProduct;
	}

	public void setParentProduct(Product parentProduct) {
		this.parentProduct = parentProduct;
	}
}
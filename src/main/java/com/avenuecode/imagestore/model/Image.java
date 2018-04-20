package com.avenuecode.imagestore.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class Image {

    @Id
    @GeneratedValue
    @Column(name="id")
    private Long id;

    @JsonIgnore
    @ManyToOne
    private Product product;

    private String description;

    private Image() { } // JPA only

    public Image(final Product product, final String description) {
        this.description = description;
        this.product = product;
    }


	public Long getId() {
        return id;
    }

    public Product getProduct() {
        return product;
    }


    public String getDescription() {
        return description;
    }

	public void setId(Long id) {
		this.id = id;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Long getParentId() {
		return product.getId();
	}
    
}
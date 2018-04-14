package com.avenuecode.imagestore.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class Image {

    @Id
    @GeneratedValue
    private Long id;

    @JsonIgnore
    @ManyToOne
    private Product product;

    private String uri;

    private String description;

    private Image() { } // JPA only

    public Image(final Product product, final String uri, final String description) {
        this.uri = uri;
        this.description = description;
        this.product = product;
    }

    public Long getId() {
        return id;
    }

    public Product getProduct() {
        return product;
    }

    public String getUri() {
        return uri;
    }

    public String getDescription() {
        return description;
    }

	@Override
	public String toString() {
		return "Image [id=" + id + ", product=" + product + ", uri=" + uri + ", description=" + description + "]";
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public void setDescription(String description) {
		this.description = description;
	}
    
    
}
package com.avenuecode.imagestore.services;

import org.springframework.data.jpa.repository.JpaRepository;

import com.avenuecode.imagestore.model.Product;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findById(String id);
    
    Optional<Product> findByName(String name);
}
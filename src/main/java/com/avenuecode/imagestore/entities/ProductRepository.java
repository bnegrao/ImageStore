package com.avenuecode.imagestore.entities;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findById(String id);
    
    Optional<Product> findByName(String name);
}
package com.avenuecode.imagestore.entities;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;

public interface ImageRepository extends JpaRepository<Image, Long> {
    Collection<Image> findByProductName(String name);        
}

package com.avenuecode.imagestore.services;

import org.springframework.data.jpa.repository.JpaRepository;

import com.avenuecode.imagestore.model.Image;

import java.util.Collection;

public interface ImageRepository extends JpaRepository<Image, Long> {
    Collection<Image> findByProductName(String name);        
}

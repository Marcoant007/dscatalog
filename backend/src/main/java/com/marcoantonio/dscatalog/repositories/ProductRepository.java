package com.marcoantonio.dscatalog.repositories;

import com.marcoantonio.dscatalog.entities.Product;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>{}
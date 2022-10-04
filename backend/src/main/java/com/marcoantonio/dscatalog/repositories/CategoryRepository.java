package com.marcoantonio.dscatalog.repositories;

import com.marcoantonio.dscatalog.entities.Category;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long>{}

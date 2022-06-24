package com.marcoantonio.dscatalog.repositories;

import com.marcoantonio.dscatalog.entities.Category;
import com.marcoantonio.dscatalog.entities.Product;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>{
    @Query("SELECT DISTINCT obj FROM Product obj INNER JOIN obj.categories categorias WHERE (COALESCE(:categories) IS NULL OR categorias IN :categories)"
    + "AND (LOWER(obj.name) LIKE LOWER(CONCAT('%',:name,'%')))")
    Page<Product> find(List<Category> categories, String name, Pageable pageble);
}
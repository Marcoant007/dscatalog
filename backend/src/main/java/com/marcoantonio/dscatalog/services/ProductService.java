package com.marcoantonio.dscatalog.services;

import java.util.Optional;

import javax.persistence.EntityNotFoundException;
import org.springframework.transaction.annotation.Transactional;

import com.marcoantonio.dscatalog.dtos.CategoryDTO;
import com.marcoantonio.dscatalog.dtos.ProductDTO;
import com.marcoantonio.dscatalog.entities.Category;
import com.marcoantonio.dscatalog.entities.Product;
import com.marcoantonio.dscatalog.repositories.CategoryRepository;
import com.marcoantonio.dscatalog.repositories.ProductRepository;
import com.marcoantonio.dscatalog.services.exceptions.DatabaseException;
import com.marcoantonio.dscatalog.services.exceptions.ResourceNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
public class ProductService {
    
    @Autowired
    private ProductRepository repository;

    @Autowired
    private CategoryRepository categoryRepository;
    
    @Transactional(readOnly = true)
    public Page<ProductDTO> findAllPaged(Long categoryId, PageRequest pageRequest) {
        Category category = (categoryId == 0) ? null : categoryRepository.getOne(categoryId);
        Page<Product> list  =  repository.find(category, pageRequest);
        Page<ProductDTO> listDTO = list.map(productObject -> new ProductDTO(productObject));
        return listDTO;
    }

    @Transactional(readOnly = true)
    public ProductDTO findById(Long id) {
        Optional<Product> productObj = repository.findById(id);
        Product entity = productObj.orElseThrow(() -> new ResourceNotFoundException("Entity not found"));
        return new ProductDTO(entity, entity.getCategories());
    }

    @Transactional
    public ProductDTO createdProduct(ProductDTO productDTO) {
        Product entity = new Product();
        copyDtoToEntity(productDTO, entity);
        entity = repository.save(entity);
        return new ProductDTO(entity);
    }

    @Transactional
    public ProductDTO updatedProduct( Long id, ProductDTO productDTO) {
       try {
        Product entity = repository.getOne(id);
        copyDtoToEntity(productDTO, entity);
        entity = repository.save(entity);
        return new ProductDTO(entity);
       } catch (EntityNotFoundException e ) {
           throw new ResourceNotFoundException("Id not Found " + id); 
       }
    }

    public void delete(Long id) {
        try {
            repository.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new ResourceNotFoundException("Id not found " + id);
        }
        catch (DataIntegrityViolationException e ){
            throw new DatabaseException("Integrity violation");
        }
    }

    private void copyDtoToEntity(ProductDTO productDTO, Product entity) {
        entity.setName(productDTO.getName());
        entity.setDescription(productDTO.getDescription());
        entity.setDate(productDTO.getDate());
        entity.setImgUrl(productDTO.getImgUrl());
        entity.setPrice(productDTO.getPrice());
        
        entity.getCategories().clear();
        for(CategoryDTO categoriesDTO : productDTO.getCategories()){
            Category category = categoryRepository.getOne(categoriesDTO.getId());
            entity.getCategories().add(category);
        }
    }
}

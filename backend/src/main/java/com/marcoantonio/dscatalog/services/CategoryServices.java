package com.marcoantonio.dscatalog.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;

import com.marcoantonio.dscatalog.dtos.CategoryDTO;
import com.marcoantonio.dscatalog.entities.Category;
import com.marcoantonio.dscatalog.repositories.CategoryRepository;
import com.marcoantonio.dscatalog.services.exceptions.ResourceNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CategoryServices {

    @Autowired
    private CategoryRepository respository;
    
    @Transactional(readOnly = true)
    public List<CategoryDTO> findAll() {
        List<Category> list  =  respository.findAll();
        List<CategoryDTO> listDTO = list.stream().map(x -> new CategoryDTO(x)).collect(Collectors.toList());
        return listDTO;
    }

    @Transactional(readOnly = true)
    public CategoryDTO findById(Long id) {
        Optional<Category> categoryObj = respository.findById(id);
        Category entity = categoryObj.orElseThrow(() -> new ResourceNotFoundException("Entity not found"));
        //orElseThrow vai rodar a minha excessão que eu criei, igual o throw do Typescript.
        return new CategoryDTO(entity);
    }

    @Transactional
    public CategoryDTO createdCategory(CategoryDTO categoryDTO) {
        //converter o DTO
        Category entity = new Category();
        entity.setName(categoryDTO.getName());
        entity = respository.save(entity);
        return new CategoryDTO(entity);
    }

    @Transactional
    public CategoryDTO updatedCategory( Long id, CategoryDTO categoryDTO) {
       try {
        Category entity = respository.getOne(id);
        entity.setName(categoryDTO.getName());
        entity = respository.save(entity);
        return new CategoryDTO(entity);
       } catch (EntityNotFoundException e ) {
           throw new ResourceNotFoundException("Id not Found " + id); 
       }
    }
}

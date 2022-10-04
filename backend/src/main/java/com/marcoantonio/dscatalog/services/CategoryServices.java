package com.marcoantonio.dscatalog.services;

import java.util.Optional;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.marcoantonio.dscatalog.dtos.CategoryDTO;
import com.marcoantonio.dscatalog.entities.Category;
import com.marcoantonio.dscatalog.repositories.CategoryRepository;
import com.marcoantonio.dscatalog.services.exceptions.DatabaseException;
import com.marcoantonio.dscatalog.services.exceptions.ResourceNotFoundException;

@Service
public class CategoryServices {

    @Autowired
    private CategoryRepository respository;
    
    @Transactional(readOnly = true)
    public Page<CategoryDTO> findAllPaged(PageRequest pageRequest) {
        Page<Category> list  =  respository.findAll(pageRequest);
        Page<CategoryDTO> listDTO = list.map(x -> new CategoryDTO(x));
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

    public void delete(Long id) {
        try {
            respository.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new ResourceNotFoundException("Id not found " + id);
        }
        catch (DataIntegrityViolationException e ){
            throw new DatabaseException("Integrity violation");
        }
    }
}

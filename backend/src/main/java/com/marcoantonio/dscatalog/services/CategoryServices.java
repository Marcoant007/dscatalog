package com.marcoantonio.dscatalog.services;

import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import com.marcoantonio.dscatalog.dtos.CategoryDTO;
import com.marcoantonio.dscatalog.entities.Category;
import com.marcoantonio.dscatalog.repositories.CategoryRepository;

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
}

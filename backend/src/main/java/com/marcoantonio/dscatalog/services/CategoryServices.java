package com.marcoantonio.dscatalog.services;

import java.util.List;

import com.marcoantonio.dscatalog.entities.Category;
import com.marcoantonio.dscatalog.repositories.CategoryRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServices {

    @Autowired
    private CategoryRepository respository;
    
    public List<Category> findAll() {
        return respository.findAll();
    }
}

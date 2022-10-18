package com.marcoantonio.dscatalog.tests.integration;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import com.marcoantonio.dscatalog.dtos.ProductDTO;
import com.marcoantonio.dscatalog.services.ProductService;
import com.marcoantonio.dscatalog.services.exceptions.ResourceNotFoundException;

@SpringBootTest
@Transactional
public class ProductServiceIT {
    
    @Autowired private ProductService productService;

    private long existingId;
    private Long noneExistingId;
    private Long countTotalProducts;
    private Long countPCGamerProducts;
    private PageRequest pageable;
  

    @BeforeEach
    void setUp() throws Exception {
        existingId = 1L;
        noneExistingId = 1000L;
        countTotalProducts = 25L;
        countPCGamerProducts = 21L;
        pageable = PageRequest.of(0, 10);
    }

    @Test
    public void findShouldReturnAllProductWhenNameNotExist(){
        String searchName = "Camera";
        Page<ProductDTO> result = productService.findAllPaged(0L, searchName,pageable);
        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    public void findShouldReturnAllProductWhenNameIsEmpty(){
        String searchName = "";
        Page<ProductDTO> findName = productService.findAllPaged(0L, searchName,pageable);
        Assertions.assertFalse(findName.isEmpty());
        Assertions.assertEquals(countTotalProducts, findName.getTotalElements());
    }

    @Test
    public void findShouldReturnProductWhenNameExistsIgnoringCase(){
        String searchName = "pC gaMeR";
        Page<ProductDTO> findName = productService.findAllPaged(0L, searchName,pageable);
        Assertions.assertFalse(findName.isEmpty());
        Assertions.assertEquals(countPCGamerProducts, findName.getTotalElements());
    }

    @Test
    public void deleteShouldThrowDatabaseExceptionWhenDepedentId(){
        Assertions.assertThrows(ResourceNotFoundException.class, () -> { 
            productService.delete(noneExistingId);
        });
    }

    @Test
    public void deleteShouldEmptyResultDataAccessExceptionWhenIdDoesNotExists(){
        Assertions.assertThrows(ResourceNotFoundException.class, () -> { 
            productService.delete(noneExistingId);
        });

    }

    @Test
    public void deleteShouldDoNothingWhenIdExists(){
        Assertions.assertDoesNotThrow(() -> { 
            productService.delete(existingId);
        });

    }
}

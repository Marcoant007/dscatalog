package com.marcoantonio.dscatalog.tests.repository.mocks;

import java.time.Instant;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.marcoantonio.dscatalog.dtos.ProductDTO;
import com.marcoantonio.dscatalog.entities.Product;


public class ProductMock {
    
    public static Product createProduct(){
      return new Product(1L, "Phone", "Good Phone", 800.00, "https://img.com/img.png", Instant.parse("2021-10-20T03:00:00Z"));
    }

    public static ProductDTO createProductDTO(){
        return new ProductDTO(createProduct());
    }
}

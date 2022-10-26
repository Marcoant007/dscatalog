package com.marcoantonio.dscatalog.tests.repository.mocks;

import java.time.Instant;

import com.marcoantonio.dscatalog.dtos.ProductDTO;
import com.marcoantonio.dscatalog.entities.Category;
import com.marcoantonio.dscatalog.entities.Product;

public class ProductMock {

  public static Product createProduct() {
    Product product = new Product(1L, "Phone", "Good Phone", 800.00, "https://img.com/img.png",
        Instant.parse("2022-10-20T03:00:00Z"));
    product.getCategories().add(new Category(1L, null));
    return product;
  }

  public static ProductDTO createProductDTO() {
    Product product = createProduct();
    return new ProductDTO(product, product.getCategories());
  }

  public static ProductDTO createProductDTO(Long id) {
    ProductDTO productDTO = createProductDTO();
    productDTO.setId(id);
    return productDTO;
  }
}

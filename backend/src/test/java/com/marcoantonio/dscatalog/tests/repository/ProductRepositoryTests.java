package com.marcoantonio.dscatalog.tests.repository;

import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.marcoantonio.dscatalog.entities.Product;
import com.marcoantonio.dscatalog.repositories.ProductRepository;
import com.marcoantonio.dscatalog.tests.repository.mocks.ProductMock;

@DataJpaTest // Coloca esse decorator para ele testar só a JPA, não precisar carregar tudo
public class ProductRepositoryTests {

    @Autowired
    private ProductRepository repository;

    private long existingId;
    private long nonExistingId;
    private long countTotalProducts;
    private long countPcGamerProducts;
    private PageRequest pageable;

    @BeforeEach
    void setUp() throws Exception {
        existingId = 1L;
        nonExistingId = 89L;
        countTotalProducts = 25L;
        countPcGamerProducts = 21L;
        pageable = PageRequest.of(0, 10);
    }

    @Test
    public void findShouldReturnAllProductWhenNameIsEmpty(){
        String searchName = "";
        Page<Product> findName = repository.find(null, searchName,pageable);
        Assertions.assertFalse(findName.isEmpty());
        Assertions.assertEquals(countTotalProducts, findName.getTotalElements());
    }

    @Test
    public void findShouldReturnProductWhenNameExistsIgnoringCase(){
        String searchName = "pC gaMeR";
        Page<Product> findName = repository.find(null, searchName,pageable);

        Assertions.assertFalse(findName.isEmpty());
        Assertions.assertEquals(countPcGamerProducts, findName.getTotalElements());
    }

    @Test
    public void findShouldReturnProductWhenNameExists(){
        String searchName = "PC GAMER";
        PageRequest pageable =  PageRequest.of(0, 10);
        Page<Product> findName = repository.find(null, searchName,pageable);

        Assertions.assertFalse(findName.isEmpty());
        Assertions.assertEquals(countPcGamerProducts, findName.getTotalElements());
    }

    @Test
    public void saveShouldPersistWithAutoIncrementWhenIdIsNull(){
        Product product = ProductMock.createProduct();
        product.setId(null);
        product = repository.save(product);
        Optional<Product> result = repository.findById(product.getId());

        Assertions.assertNotNull(product.getId());
        Assertions.assertEquals(countTotalProducts + 1, product.getId());
        Assertions.assertTrue(result.isPresent());
        Assertions.assertSame(result.get(), product);
    }

    @Test
    public void deleteShouldDeleteObjectWhenIdExists() {
        repository.deleteById(existingId);
        Optional<Product> result = repository.findById(existingId);
        result.isPresent();
        Assertions.assertFalse(result.isPresent());
    }

    @Test
    public void deleteShouldThrowEmptyResultDataAccessExceptionWhenIdDoesNotExist() {
        Assertions.assertThrows(EmptyResultDataAccessException.class, () -> {
            repository.deleteById(nonExistingId);
        });
    }

}

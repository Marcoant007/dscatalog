package com.marcoantonio.dscatalog.tests.services;

import com.marcoantonio.dscatalog.dtos.ProductDTO;
import com.marcoantonio.dscatalog.entities.Product;
import com.marcoantonio.dscatalog.repositories.ProductRepository;
import com.marcoantonio.dscatalog.services.ProductService;
import com.marcoantonio.dscatalog.services.exceptions.DatabaseException;
import com.marcoantonio.dscatalog.services.exceptions.ResourceNotFoundException;
import com.marcoantonio.dscatalog.tests.repository.mocks.ProductMock;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
public class ProductServiceTests {

  @InjectMocks
  private ProductService service;

  @Mock
  private ProductRepository repository;

  private long existingId;
  private Long noneExistingId;
  private Long dependentId;
  private Product product;
  private ProductDTO productDTO;
  private PageImpl<Product> page;
  private PageImpl<ProductDTO> pageDTO;

  @BeforeEach
  void setUp() throws Exception {
    existingId = 1L;
    noneExistingId = 1000L;
    dependentId = 4L;
    product = ProductMock.createProduct();
    productDTO = ProductMock.createProductDTO();
    page = new PageImpl<>(List.of(product));
    pageDTO = new PageImpl<>(List.of(productDTO));

    Mockito
      .when(
        repository.find(
          ArgumentMatchers.any(),
          ArgumentMatchers.anyString(),
          ArgumentMatchers.any()
        )
      )
      .thenReturn(page);
    Mockito.when(repository.save(ArgumentMatchers.any())).thenReturn(product);
    Mockito
      .when(repository.findById(noneExistingId))
      .thenReturn(Optional.empty());
    Mockito
      .when(repository.findById(existingId))
      .thenReturn(Optional.of(product)); // QUANDO CHAMAR O FINDBY ID COM ID EXISTENTE O MOCKITO DEVE RETORNAR UM OPTIONAL DO PRODUTO

    Mockito.doNothing().when(repository).deleteById(existingId); // quando chamar o deletebyId com id existente, esse método não fará nada.

    Mockito
      .doThrow(EmptyResultDataAccessException.class)
      .when(repository)
      .deleteById(noneExistingId); // quando chamar um delete by id com um id que não existe, espero que o mockito retorne uma throw
    Mockito
      .doThrow(DataIntegrityViolationException.class)
      .when(repository)
      .deleteById(dependentId);
  }

  @Test
  public void deleteShouldThrowDatabaseExceptionWhenDepedentId() {
    Assertions.assertThrows(
      DatabaseException.class,
      () -> {
        service.delete(dependentId);
      }
    );

    Mockito.verify(repository, Mockito.times(1)).deleteById(dependentId);
  }

  @Test
  public void deleteShouldEmptyResultDataAccessExceptionWhenIdDoesNotExists() {
    Assertions.assertThrows(
      ResourceNotFoundException.class,
      () -> {
        service.delete(noneExistingId);
      }
    );

    Mockito.verify(repository, Mockito.times(1)).deleteById(noneExistingId);
  }

  @Test
  public void deleteShouldDoNothingWhenIdExists() {
    Assertions.assertDoesNotThrow(() -> {
      service.delete(existingId);
    });
    Mockito.verify(repository, Mockito.times(1)).deleteById(existingId);
  }
}

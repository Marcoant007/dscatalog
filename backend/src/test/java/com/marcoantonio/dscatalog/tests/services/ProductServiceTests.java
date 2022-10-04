package com.marcoantonio.dscatalog.tests.services;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.marcoantonio.dscatalog.repositories.ProductRepository;
import com.marcoantonio.dscatalog.services.ProductService;
import com.marcoantonio.dscatalog.services.exceptions.DatabaseException;
import com.marcoantonio.dscatalog.services.exceptions.ResourceNotFoundException;

@ExtendWith(SpringExtension.class)
public class ProductServiceTests {

    @InjectMocks
    private ProductService service;

    @Mock
    private ProductRepository repository;

    private long existingId;
    private Long noneExistingId;
    private Long dependentId;

    @BeforeEach
    void setUp() throws Exception {
        existingId = 1L;
        noneExistingId = 1000L;
        dependentId = 4L;
        doNothing().when(repository).deleteById(existingId); // quando chamar o deletebyId com id existente, esse método não fará nada.
        doThrow(EmptyResultDataAccessException.class).when(repository).deleteById(noneExistingId); // quando chamar um delete by id com um id que não existe, espero que o mockito retorne uma throw
        doThrow(DataIntegrityViolationException.class).when(repository).deleteById(dependentId);
    }

    
    @Test
    public void deleteShouldDatabaseExceptionWhenIdDoesNotExists(){
        Assertions.assertThrows(DatabaseException.class, () -> { 
            service.delete(dependentId);
        });

        verify(repository, Mockito.times(1)).deleteById(dependentId);
    }

    @Test
    public void deleteShouldEmptyResultDataAccessExceptionWhenIdDoesNotExists(){
        Assertions.assertThrows(ResourceNotFoundException.class, () -> { 
            service.delete(noneExistingId);
        });

        verify(repository, Mockito.times(1)).deleteById(noneExistingId);
    }

    @Test
    public void deleteShouldDoNothingWhenIdExists(){
        Assertions.assertDoesNotThrow(() -> { 
            service.delete(existingId);
        });

        verify(repository, Mockito.times(1)).deleteById(existingId);
    }

}

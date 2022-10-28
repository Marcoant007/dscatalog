package com.marcoantonio.dscatalog.tests.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.json.JacksonJsonParser;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.amazonaws.partitions.model.Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.marcoantonio.dscatalog.dtos.ProductDTO;
import com.marcoantonio.dscatalog.services.ProductService;
import com.marcoantonio.dscatalog.services.exceptions.ResourceNotFoundException;
import com.marcoantonio.dscatalog.tests.repository.mocks.ProductMock;

@SpringBootTest
@AutoConfigureMockMvc
public class ProductControllerTests {

	@Autowired
	private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

	@Value("${security.oauth2.client.cliente-id}")
	private String clientId;
	
	@Value("${security.oauth2.client.cliente-secret}")
	private String clientSecret;

    private Long existingId;
    private long noneExistingid;
    private ProductDTO newProductDTO;
    private ProductDTO existingProductDTO;
    private PageImpl<ProductDTO> page;

    @BeforeEach
    public void setUp() throws Exception{
        existingId = 1L;
        noneExistingid = 2L;
        newProductDTO = ProductMock.createProductDTO(null);
        existingProductDTO = ProductMock.createProductDTO(existingId);
        
        page = new PageImpl<>(List.of(existingProductDTO));

        when(productService.findById(existingId)).thenReturn(existingProductDTO);
        when(productService.findById(noneExistingid)).thenThrow(ResourceNotFoundException.class);
        when(productService.findAllPaged( any(), anyString(), any())).thenReturn(page);
    }

    @Test
    public void findByIdShouldReturnProductWhenIdExists() throws Exception{
       ResultActions result = mockMvc.perform(get("/products/{id}", existingId).accept(MediaType.APPLICATION_JSON));
       result.andExpect(status().isOk());
       result.andExpect(jsonPath("$.id").exists());
       result.andExpect(jsonPath("$.id").value(existingId));
    }
    
    @Test
    public void findByIdShouldReturnProductWhenDoesNotExists() throws Exception{
        ResultActions result = mockMvc.perform(get("/products/{id}", noneExistingid).accept(MediaType.APPLICATION_JSON));
        result.andExpect(status().isNotFound());
    }


    @Test
    public void findAllShouldReturnPage() throws Exception{
        ResultActions result = mockMvc.perform(get("/products", noneExistingid).accept(MediaType.APPLICATION_JSON));
        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.content").exists());
    }
    
	private String obtainAccessToken(String username, String password) throws Exception {
	    MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
	    params.add("grant_type", "password");
	    params.add("client_id", clientId);
	    params.add("username", username);
	    params.add("password", password);
	 
	    ResultActions result 
	    	= mockMvc.perform(post("/oauth/token")
	    		.params(params)
	    		.with(httpBasic(clientId, clientSecret))
	    		.accept("application/json;charset=UTF-8"))
	        	.andExpect(status().isOk())
	        	.andExpect(content().contentType("application/json;charset=UTF-8"));
	 
	    String resultString = result.andReturn().getResponse().getContentAsString();
	 
	    JacksonJsonParser jsonParser = new JacksonJsonParser();
	    return jsonParser.parseMap(resultString).get("access_token").toString();
	}	
}
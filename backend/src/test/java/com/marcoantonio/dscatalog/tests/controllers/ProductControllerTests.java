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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.marcoantonio.dscatalog.dtos.ProductDTO;
import com.marcoantonio.dscatalog.services.ProductService;
import com.marcoantonio.dscatalog.services.exceptions.DatabaseException;
import com.marcoantonio.dscatalog.services.exceptions.ResourceNotFoundException;
import com.marcoantonio.dscatalog.tests.repository.mocks.ProductMock;

@SpringBootTest
@AutoConfigureMockMvc
public class ProductControllerTests {

	@Autowired
	private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProductService productService;

	@Value("${security.oauth2.client.cliente-id}")
	private String clientId;
	
	@Value("${security.oauth2.client.cliente-secret}")
	private String clientSecret;

    private Long existingId;
    private Long noneExistingid;
    private Long depedentId;
    private ProductDTO newProductDTO;
    private ProductDTO existingProductDTO;
    private PageImpl<ProductDTO> page;
    private String operatorUsername;
    private String operatorPassword;

    @BeforeEach
    public void setUp() throws Exception{
        operatorUsername = "alex@gmail.com";
        operatorPassword = "123456";
        existingId = 1L;
        noneExistingid = 2L;
        depedentId = 3L;
        newProductDTO = ProductMock.createProductDTO(null);
        existingProductDTO = ProductMock.createProductDTO(existingId);
        
        page = new PageImpl<>(List.of(existingProductDTO));

        when(productService.findById(existingId)).thenReturn(existingProductDTO);
        when(productService.findById(noneExistingid)).thenThrow(ResourceNotFoundException.class);
        when(productService.findAllPaged( any(), anyString(), any())).thenReturn(page);
        when(productService.createdProduct(any())).thenReturn(existingProductDTO);
        when(productService.updatedProduct(eq(existingId), any())).thenReturn(existingProductDTO);
        when(productService.updatedProduct(eq(noneExistingid), any())).thenThrow(ResourceNotFoundException.class);
        doNothing().when(productService).delete(existingId);
        doThrow(ResourceNotFoundException.class).when(productService).delete(noneExistingid);
        doThrow(DatabaseException.class).when(productService).delete(depedentId);
    }

    @Test
    public void findByIdShouldReturnProductWhenIdExists() throws Exception{
       ResultActions result = mockMvc.perform(get("/products/{id}", existingId).accept(MediaType.APPLICATION_JSON));
       result.andExpect(status().isOk());
       result.andExpect(jsonPath("$.id").exists());
       result.andExpect(jsonPath("$.id").value(existingId));
    }

    @Test
    public void updateShouldReturnNotFoundWhenIdDoesNotExists() throws Exception{
        String accessToken = obtainAccessToken(operatorUsername, operatorPassword);
        String jsonBody = objectMapper.writeValueAsString(newProductDTO);

        ResultActions result = mockMvc.perform(put("/products/{id}", noneExistingid)
        .header("Authorization", "Bearer " + accessToken)
        .content(jsonBody)
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON));
        result.andExpect(status().isNotFound());
    }

    @Test
    public void updateShouldReturnProductDtoWhenIdExists() throws Exception{
        String accessToken = obtainAccessToken(operatorUsername, operatorPassword);
        String jsonBody = objectMapper.writeValueAsString(newProductDTO);
        String expectedName = newProductDTO.getName();
        Double expectedPrice = newProductDTO.getPrice();

        ResultActions result = mockMvc.perform(put("/products/{id}", existingId)
        .header("Authorization", "Bearer " + accessToken)
        .content(jsonBody)
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON));
        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.id").exists());
        result.andExpect(jsonPath("$.id").value(existingId));
        result.andExpect(jsonPath("$.name").value(expectedName));
        result.andExpect(jsonPath("$.price").value(expectedPrice));
    }

    @Test
    public void createShouldReturnProductDtoWhenIdExists() throws Exception {
        String accessToken = obtainAccessToken(operatorUsername, operatorPassword);
        String jsonBody = objectMapper.writeValueAsString(newProductDTO);

        ResultActions result = mockMvc.perform(post("/products").accept(MediaType.APPLICATION_JSON)
        .header("Authorization", "Bearer " + accessToken)
        .content(jsonBody)
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON));
        result.andExpect(status().isCreated());
    }

    @Test
    public void createShouldReturnUnprocessableWhenPriceIsNegative() throws Exception {
        String accessToken = obtainAccessToken(operatorUsername, operatorPassword);
        newProductDTO.setPrice(-10.0);
        String jsonBody = objectMapper.writeValueAsString(newProductDTO);
       
        ResultActions result = mockMvc.perform(post("/products").accept(MediaType.APPLICATION_JSON)
        .header("Authorization", "Bearer " + accessToken)
        .content(jsonBody)
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isUnprocessableEntity());
    }

    @Test
    public void deleteShouldReturnNoContentWhenIdExists() throws Exception {
        String accessToken = obtainAccessToken(operatorUsername, operatorPassword);
        ResultActions result = mockMvc.perform(delete("/products/{id}", existingId).accept(MediaType.APPLICATION_JSON)
        .header("Authorization", "Bearer " + accessToken)
        .accept(MediaType.APPLICATION_JSON));
        result.andExpect(status().isNoContent());
    }

    @Test
    public void deleteShouldReturnNotFoundWhenIdNotExists() throws  Exception {
        String accessToken = obtainAccessToken(operatorUsername, operatorPassword);
        ResultActions result = mockMvc.perform(delete("/products/{id}", noneExistingid).accept(MediaType.APPLICATION_JSON)
        .header("Authorization", "Bearer " + accessToken)
        .accept(MediaType.APPLICATION_JSON));
        result.andExpect(status().isNotFound());
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
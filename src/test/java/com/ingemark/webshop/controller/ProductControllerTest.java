package com.ingemark.webshop.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ingemark.webshop.model.Product;
import com.ingemark.webshop.service.ProductService;
import org.assertj.core.util.Lists;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import javax.persistence.EntityNotFoundException;
import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(ProductController.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Captor
    private ArgumentCaptor<Product> argumentCaptor;

    @MockBean
    private ProductService productService;

    private final String productUrl = "/api/products";
    private static Product test;
    private static Product test2;

    @BeforeAll
    void prepare() {
        test = Product.builder().id(1L).code("1234567890").name("product").priceHrk(BigDecimal.TEN)
                .description("description of product").isAvailable(true).build();
        test2 = Product.builder().id(2L).code("0000012345").name("new product").priceHrk(BigDecimal.valueOf(15.75))
                .description("description of a second product").isAvailable(true).build();
    }

    @Test
    @DisplayName("Should get a list of existing products")
    public void shouldGetAllProducts() throws Exception {
        when(productService.getAll()).thenReturn(Lists.newArrayList(
                test, test2
        ));

        this.mockMvc
                .perform(get(productUrl))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*]", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].code", is("1234567890")))
                .andExpect(jsonPath("$[0].name", is("product")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].code", is("0000012345")))
                .andExpect(jsonPath("$[1].name", is("new product")));
    }

    @Test
    @DisplayName("Should get empty list even if products dont exist")
    public void shouldGetAllProducts_IfEmpty() throws Exception {
        when(productService.getAll()).thenReturn(Lists.newArrayList());

        this.mockMvc
                .perform(get(productUrl))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*]", hasSize(0)));
    }

    @Test
    @DisplayName("Should get existing product")
    public void shouldGetOneProduct() throws Exception {
        when(productService.getOne(anyLong())).thenReturn(test);

        this.mockMvc
                .perform(get(productUrl + "/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.code", is("1234567890")))
                .andExpect(jsonPath("$.name", is("product")));
    }


    @Test
    @DisplayName("Should return NOT FOUND error on getting unknown id")
    public void shouldReturn404_OnGetOneProduct() throws Exception {
        when(productService.getOne(anyLong()))
                .thenThrow(new EntityNotFoundException("Product with provided id not found"));

        this.mockMvc
                .perform(get(productUrl + "/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should create new product")
    public void shouldCreateNewProduct() throws Exception {
        Product testWithoutId = Product.builder().code("0000012345").name("new product").priceHrk(BigDecimal.valueOf(15.75))
                .description("description of a second product").isAvailable(true).build();

        when(productService.save(any(Product.class))).thenReturn(test2);

        this.mockMvc
                .perform(post(productUrl)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testWithoutId)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(2)))
                .andExpect(jsonPath("$.code", is("0000012345")))
                .andExpect(jsonPath("$.name", is("new product")));

        verify(productService, times(1)).save(argumentCaptor.capture());
        assertThat(argumentCaptor.getValue().getName()).isEqualTo("new product");
        assertThat(argumentCaptor.getValue().getCode()).isEqualTo("0000012345");
    }

    @Test
    @DisplayName("Should fail on validation when trying to create product with invalid fields")
    public void shouldFailOnValidation_OnCreateProduct() throws Exception {
        Product failTest = Product.builder().name("new product")
                .priceHrk(BigDecimal.valueOf(-15)).isAvailable(true).build();

        this.mockMvc
                .perform(post(productUrl)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(failTest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", Matchers.startsWith("Validation failed")))
                .andExpect(jsonPath("$.message", Matchers.containsString("must not be null")))
                .andExpect(jsonPath("$.message",
                        Matchers.containsString("must be greater than or equal to 0")));

        // assert that method was not called
        verify(productService, times(0)).save(forClass(Product.class).capture());
    }

    @Test
    @DisplayName("Should update the product")
    public void updateProduct() throws Exception {
        when(productService.update(anyLong(), any(Product.class))).thenReturn(test2);

        this.mockMvc
                .perform(put(productUrl + "/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(test2)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(2)))
                .andExpect(jsonPath("$.code", is("0000012345")))
                .andExpect(jsonPath("$.name", is("new product")));

        verify(productService, times(1)).update(anyLong(), argumentCaptor.capture());
        assertThat(argumentCaptor.getValue().getName()).isEqualTo("new product");
        assertThat(argumentCaptor.getValue().getCode()).isEqualTo("0000012345");
    }

    @Test
    @DisplayName("Should fail on validation when trying to update product with invalid fields")
    public void shouldFailOnValidation_OnUpdateProduct() throws Exception {
        Product failTest = Product.builder().id(1L).name("new product")
                .priceHrk(BigDecimal.valueOf(-15)).isAvailable(true).build();

        this.mockMvc
                .perform(put(productUrl + "/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(failTest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", Matchers.startsWith("Validation failed")))
                .andExpect(jsonPath("$.message", Matchers.containsString("must not be null")))
                .andExpect(jsonPath("$.message",
                        Matchers.containsString("must be greater than or equal to 0")));

        // assert that method was not called
        verify(productService, times(0)).save(forClass(Product.class).capture());
    }

    @Test
    @DisplayName("Should return NOT FOUND error on updating unknown id")
    public void shouldReturn404_OnUpdateProduct() throws Exception {
        when(productService.update(anyLong(), any()))
                .thenThrow(new EntityNotFoundException("Product with provided id not found"));

        this.mockMvc
                .perform(put(productUrl + "/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(test)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Product with provided id not found")));
    }

    @Test
    @DisplayName("Should delete product with a given id")
    public void deleteProduct() throws Exception {
        this.mockMvc
                .perform(delete(productUrl + "/1"))
                .andExpect(status().isNoContent());

        verify(productService, times(1)).delete(forClass(Long.class).capture());
    }

    @Test
    @DisplayName("Should return NotFound when given the id that doesn't exist")
    public void deleteProductFail_IfDoesntExist() throws Exception {
        doThrow(new EntityNotFoundException()).when(productService).delete(anyLong());
        this.mockMvc
                .perform(delete(productUrl + "/10"))
                .andExpect(status().isNotFound());

        verify(productService, times(1)).delete(forClass(Long.class).capture());
    }

}

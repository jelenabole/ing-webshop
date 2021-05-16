package com.ingemark.webshop.service;

import com.ingemark.webshop.model.Product;
import com.ingemark.webshop.repository.ProductRepository;
import org.assertj.core.api.Assertions;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ProductServiceTest {
    private ProductService productService;
    private ProductRepository productRepository;

    private static Product test;
    private static Product test2;

    @BeforeAll
    void prepare() {
        test = Product.builder().id(1L).code("1234567890").name("product").priceHrk(BigDecimal.TEN)
                .description("description of product").isAvailable(true).build();
        test2 = Product.builder().id(2L).code("0000012345").name("second product").priceHrk(BigDecimal.valueOf(15.75))
                .description("description of a second product").isAvailable(true).build();
    }

    @BeforeEach
    void setupService() {
        productRepository = mock(ProductRepository.class);
        productService = new ProductService(productRepository);
    }

    @Test
    void testGetAll() {
        when(productRepository.findAll()).thenReturn(Lists.newArrayList(
                test, test2
        ));

        List<Product> result = productService.getAll();

        assertThat(result.isEmpty()).isFalse();
        assertThat(result.size()).isEqualTo(2);
        assertThat(result.get(0).getCode()).isEqualTo("1234567890");
        assertThat(result.get(1).getCode()).isEqualTo("0000012345");
    }

    @Test
    void testGetAll_ifEmtpy() {
        when(productRepository.findAll()).thenReturn(Lists.newArrayList());
        List<Product> result = productService.getAll();

        assertThat(result).isNotNull();
        assertThat(result.isEmpty()).isTrue();
        assertThat(result.size()).isEqualTo(0);
    }

    @Test
    void testGetOne() {
        Long objectID = 1L;
        when(productRepository.findById(objectID)).thenReturn(Optional.of(test));

        Product result = productService.getOne(objectID);

        assertThat(result.getId()).isEqualTo(objectID);
        assertThat(result.getCode()).isEqualTo("1234567890");
    }

    @Test
    void testGetOne_ObjectNotFound() {
        Long objectID = 3L;
        when(productRepository.findById(objectID))
                .thenThrow(new EntityNotFoundException("Product with that id not found"));
        Exception exception = assertThrows(EntityNotFoundException.class, () -> productService.getOne(objectID));

        verify(productRepository, times(1)).findById(anyLong());
        assertThat(exception.getMessage()).startsWith("Product with that id not found");
    }

    @Test
    void testSave() {
        Product productWithoutId = Product.builder().code("1234567890").name("product").priceHrk(BigDecimal.TEN)
                .description("description of product").isAvailable(true).build();
        when(productRepository.save(any(Product.class))).thenReturn(test);

        Product result = productService.save(productWithoutId);

        // objects are the same if they have same values (id is ignored)
        Assertions.assertThat(result).isEqualTo(productWithoutId);
        Assertions.assertThat(result.getId()).isEqualTo(1L);
        Assertions.assertThat(result.getCode()).isEqualTo("1234567890");
    }

    @Test
    void testUpdate() {
        Long objectID = 1L;
        Product update = Product.builder().id(objectID).code("0000000000").name("new name").priceHrk(BigDecimal.ZERO)
                .description("description of product").isAvailable(false).build();
        when(productRepository.findById(objectID)).thenReturn(Optional.of(test));
        when(productRepository.save(any(Product.class))).thenReturn(test2);

        Product result = productService.update(objectID, update);
        verify(productRepository, times(1)).findById(anyLong());
        verify(productRepository, times(1)).save(any(Product.class));

        // change od "code" is ignored, so the objects are not equal
        Assertions.assertThat(result).isNotEqualTo(update);
        Assertions.assertThat(result.getCode()).isEqualTo("1234567890");
        Assertions.assertThat(result.getId()).isEqualTo(objectID);
        Assertions.assertThat(result.getName()).isEqualTo("new name");
        Assertions.assertThat(result.isAvailable()).isEqualTo(false);
        Assertions.assertThat(result.getPriceHrk()).isEqualTo(BigDecimal.ZERO);
    }

    @Test
    void testUpdate_ObjectNotFound() {
        Long objectID = 3L;
        when(productRepository.findById(objectID))
                .thenThrow(new EntityNotFoundException("Product with that id not found"));
        Exception exception = assertThrows(EntityNotFoundException.class, () -> productService.update(objectID, test));

        verify(productRepository, times(1)).findById(anyLong());
        assertThat(exception.getMessage()).startsWith("Product with that id not found");
    }

    @Test
    void testDelete() {
        doNothing().when(productRepository).deleteById(anyLong());
        productRepository.deleteById(1L);
        verify(productRepository, times(1)).deleteById(anyLong());
    }
}
package com.ingemark.webshop.controller;

import com.ingemark.webshop.model.Product;
import com.ingemark.webshop.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);
    private final ProductService productService;

    @GetMapping
    public List<Product> getAllProducts() {
        logger.info("getAllProducts is called");
        return productService.getAll();
    }

    @GetMapping("/{id}")
    public Product getProduct(@PathVariable Long id) {
        logger.info("getProduct is called - with id: {}", id);
        return productService.getOne(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Product createProduct(@Valid @RequestBody Product product) {
        logger.info("createProduct is called - with args: {}", product.toString());
        return productService.save(product);
    }

    @PutMapping("/{id}")
    public Product updateProduct(@PathVariable Long id, @Valid @RequestBody Product product) {
        logger.info("updateProduct is called - with id: {}, and args: {}", id, product.toString());
        return productService.update(id, product);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProduct(@PathVariable Long id) {
        logger.info("deleteProduct is called - with product id: {}", id);
        productService.delete(id);
    }

}

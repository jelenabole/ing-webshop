package com.ingemark.webshop.service;

import com.ingemark.webshop.model.Product;
import com.ingemark.webshop.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    /**
     * Returns all existing products.
     *
     * @return fetched products as a List
     */
    public List<Product> getAll() {
        List<Product> list = new ArrayList<>();
        productRepository.findAll().iterator().forEachRemaining(list::add);
        return list;
    }

    /**
     * Returns all products with a given IDs as a List.
     *
     * @param ids IDs of the products to fetch
     * @return products with given IDs
     */
    public List<Product> getAllByIds(List<Long> ids) {
        List<Product> list = new ArrayList<>();
        productRepository.findAllById(ids).iterator().forEachRemaining(list::add);
        return list;
    }

    /**
     * Returns one product with a given ID.
     *
     * @param id ID of the product to fetch
     * @return fetched product
     * @throws RuntimeException thrown if product with provided ID doesn't exist
     */
    public Product getOne(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product with provided id not found"));
    }

    /**
     * Saves new product.
     *
     * @param product product to save
     * @return saved product
     */
    public Product save(Product product) {
        return productRepository.save(product);
    }

    /**
     * Updates data of an existing product. Only updatable fields will be changed - value of "code" field
     * will be ignored even if its different in input object.
     *
     * @param id ID of the product to update
     * @param product object with new values
     * @return updated product
     * @throws RuntimeException thrown if product with provided ID doesn't exist
     */
    @Transactional
    public Product update(Long id, Product product) {
        Product newInfo = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product with provided id not found"));

        // code change ignored
        newInfo.setName(product.getName());
        newInfo.setDescription(product.getDescription());
        newInfo.setAvailable(product.isAvailable());
        newInfo.setPriceHrk(product.getPriceHrk());

        productRepository.save(newInfo);
        return newInfo;
    }

    /**
     * Deletes one product by ID.
     *
     * @param id ID of the product to delete
     * @throws RuntimeException thrown if product doesn't exist
     */
    public void delete(Long id) {
        productRepository.deleteById(id);
    }

}

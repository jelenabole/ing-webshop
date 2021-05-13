package com.ingemark.webshop.service;

import com.ingemark.webshop.exception.ObjectNotFoundException;
import com.ingemark.webshop.model.Order;
import com.ingemark.webshop.model.Product;
import com.ingemark.webshop.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public Iterable<Product> getAll() {
        return productRepository.findAll();
    }

    public Product getOne(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException(Order.class.getSimpleName(), id));
    }

    public Product save(Product product) {
        return productRepository.save(product);
    }

    @Transactional
    public Product update(Long id, Product product) {
        Product newInfo = productRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException(Order.class.getSimpleName(), id));

        // code change ignored
        newInfo.setName(product.getName());
        newInfo.setDescription(product.getDescription());
        newInfo.setIsAvailable(product.getIsAvailable());
        newInfo.setPriceHrk(product.getPriceHrk());

        return productRepository.save(newInfo);
    }

    public void delete(Long id) {
        productRepository.deleteById(id);
    }

}

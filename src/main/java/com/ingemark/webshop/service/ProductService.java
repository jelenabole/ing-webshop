package com.ingemark.webshop.service;

import com.ingemark.webshop.model.Product;
import com.ingemark.webshop.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public Iterable<Product> getAll() {
        return productRepository.findAll();
    }

    public Optional<Product> getOne(long id) {
        return productRepository.findById(id);
    }

    public Product save(Product product) {
        try {
            return productRepository.save(product);
        } catch (DataIntegrityViolationException ex) {
            return null;
        }
    }

    @Transactional
    public Product update(Long id, Product product) {
        Optional<Product> prodInfo = productRepository.findById(id);
        if (prodInfo.isPresent()) {
            Product prodData = prodInfo.orElse(null);

            prodData.setName(product.getName());
            prodData.setDescription(product.getDescription());
            prodData.setIsAvailable(product.getIsAvailable());
            prodData.setPriceHrk(product.getPriceHrk());

            return productRepository.save(prodData);
        } else {
            return null;
        }
    }

    public boolean delete(long id) {
        try {
            productRepository.deleteById(id);
            return true;
        } catch (EmptyResultDataAccessException ex) {
            return false;
        }
    }

}

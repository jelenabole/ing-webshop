package com.ingemark.webshop.service;

import com.ingemark.webshop.exception.ObjectNotFoundException;
import com.ingemark.webshop.model.Product;
import com.ingemark.webshop.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public List<Product> getAll() {
        List<Product> list = new ArrayList<>();
        productRepository.findAll().iterator().forEachRemaining(list::add);
        return list;
    }

    public List<Product> getAllByIds(List<Long> ids) {
        List<Product> list = new ArrayList<>();
        productRepository.findAllById(ids).iterator().forEachRemaining(list::add);
        return list;
    }

    public Product getOne(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException(Product.class.getSimpleName(), id));
    }

    public Product save(Product product) {
        return productRepository.save(product);
    }

    @Transactional
    public Product update(Long id, Product product) {
        Product newInfo = productRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException(Product.class.getSimpleName(), id));

        // code change ignored
        newInfo.setName(product.getName());
        newInfo.setDescription(product.getDescription());
        newInfo.setIsAvailable(product.getIsAvailable());
        newInfo.setPriceHrk(product.getPriceHrk());

        productRepository.save(newInfo);
        return newInfo;
    }

    public void delete(Long id) {
        productRepository.deleteById(id);
    }

}

package com.ingemark.webshop.repository;

import com.ingemark.webshop.model.Product;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.stereotype.Repository;

@Repository
@RestResource(exported = false)
public interface ProductRepository extends CrudRepository<Product, Long> {

}

package com.ingemark.webshop.repository;

import com.ingemark.webshop.model.Order;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RestResource;

@RestResource(exported = false)
public interface CustomerRepository extends CrudRepository<Order, Long> {

}

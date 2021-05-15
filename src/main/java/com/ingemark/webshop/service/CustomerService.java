package com.ingemark.webshop.service;

import com.ingemark.webshop.exception.ObjectNotFoundException;
import com.ingemark.webshop.model.Customer;
import com.ingemark.webshop.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;

    public Iterable<Customer> getAll() {
        return customerRepository.findAll();
    }

    public Customer getOne(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException(Customer.class.getSimpleName(), id));
    }

    public Customer getTestUser() {
        Customer customer;
        List<Customer> list = new ArrayList<>();
        getAll().iterator().forEachRemaining(list::add);

        if (list.isEmpty()) {
            return create();
        }

        return list.get(0);
    }

    public Customer create() {
        Customer customer = new Customer();
        customer.setEmail("email@email.com");
        customer.setFirstName("John");
        customer.setLastName("Doe");

        return customerRepository.save(customer);
    }

}

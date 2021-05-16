package com.ingemark.webshop.service;

import com.ingemark.webshop.model.Customer;
import com.ingemark.webshop.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;

    /**
     * Returns all existing customer.
     *
     * @return fetched customer as a List
     */
    public Iterable<Customer> getAll() {
        return customerRepository.findAll();
    }

    /**
     * Returns one customer with a given ID.
     *
     * @param id ID of the customer to fetch
     * @return fetched customer
     * @throws RuntimeException thrown if customer with provided ID does not exist
     */
    public Customer getOne(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Customer with provided id not found"));
    }

    /**
     * Returns an existing test customer. Gets first customer from the database, or, if the it's empty,
     * creates a new customer to save and return.
     * This function is for testing purposes, since customer operations are omitted.
     * @return test customer
     */
    public Customer getTestCustomer() {
        List<Customer> list = new ArrayList<>();
        getAll().iterator().forEachRemaining(list::add);

        if (list.isEmpty()) {
            return create();
        }

        return list.get(0);
    }


    /**
     * Creates new customer with predefined values.
     * @return test customer
     */
    public Customer create() {
        Customer customer = new Customer();
        customer.setEmail("email@email.com");
        customer.setFirstName("John");
        customer.setLastName("Doe");

        return customerRepository.save(customer);
    }

}

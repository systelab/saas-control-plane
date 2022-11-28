package com.systelab.saas.service;

import com.systelab.saas.event.CustomerCreatedEvent;
import com.systelab.saas.exception.CustomerNotFoundException;
import com.systelab.saas.model.customer.Customer;
import com.systelab.saas.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;

    private final ApplicationEventPublisher applicationEventPublisher;


    public Page<Customer> getAllCustomers(Pageable pageable) {
        final PageRequest page = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.Direction.ASC, "name");
        return this.customerRepository.findAll(page);
    }

    public Customer getCustomer(UUID customerId) {
        return this.customerRepository.findById(customerId).orElseThrow(() -> new CustomerNotFoundException(customerId));
    }

    public Customer createCustomer(Customer customer) {
        Customer saved = this.customerRepository.save(customer);
        CustomerCreatedEvent customerCreated = new CustomerCreatedEvent(this, saved);
        applicationEventPublisher.publishEvent(customerCreated);
        return saved;
    }

    public Customer updateCustomer(UUID id, Customer p) {
        return this.customerRepository.findById(id).map(existing -> {
            existing.setName(p.getName());
            existing.setEmail(p.getEmail());
            existing.setAddress(p.getAddress());
            existing.setDatabaseServer(p.getDatabaseServer());
            existing.setApplicationServer(p.getApplicationServer());
            return this.customerRepository.save(existing);
        }).orElseThrow(() -> new CustomerNotFoundException(id));
    }

    public Customer updateCustomerDatabase(UUID id, Customer p) {
        return this.customerRepository.findById(id).map(existing -> {
            existing.setDatabaseServer(p.getDatabaseServer());
            return this.customerRepository.save(existing);
        }).orElseThrow(() -> new CustomerNotFoundException(id));
    }

    public Customer updateCustomerApplicationServer(UUID id, Customer p) {
        return this.customerRepository.findById(id).map(existing -> {
            existing.setApplicationServer(p.getApplicationServer());
            return this.customerRepository.save(existing);
        }).orElseThrow(() -> new CustomerNotFoundException(id));
    }


    public Customer removeCustomer(UUID id) {
        return this.customerRepository.findById(id).map(existing -> {
            customerRepository.delete(existing);
            return existing;
        }).orElseThrow(() -> new CustomerNotFoundException(id));
    }

}

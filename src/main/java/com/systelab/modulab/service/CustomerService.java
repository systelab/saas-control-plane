package com.systelab.modulab.service;

import com.systelab.modulab.exception.CustomerNotFoundException;
import com.systelab.modulab.model.customer.Customer;
import com.systelab.modulab.repository.CustomerRepository;
import com.systelab.modulab.service.aws.AMI;
import com.systelab.modulab.service.aws.EC2Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final EC2Service ec2Service;

    @Autowired
    public CustomerService(CustomerRepository customerRepository, EC2Service ec2Service) {
        this.customerRepository = customerRepository;
        this.ec2Service = ec2Service;

    }

    public Page<Customer> getAllCustomers(Pageable pageable) {
        final PageRequest page = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.Direction.ASC, "name");
        return this.customerRepository.findAll(page);
    }

    public Customer getCustomer(UUID customerId) {
        return this.customerRepository.findById(customerId).orElseThrow(() -> new CustomerNotFoundException(customerId));
    }

    public Customer createCustomer(Customer p) {
        String instance=this.ec2Service.createInstance(p.getNickname(), AMI.AMAZON_LINUX2_AMI);
        p.setApplicationServerInstance(instance);
        Customer saved=this.customerRepository.save(p);
        return saved;
    }

    public Customer updateCustomer(UUID id, Customer p) {
        return this.customerRepository.findById(id).map(existing -> {
            p.setId(id);
            return this.customerRepository.save(p);
        }).orElseThrow(() -> new CustomerNotFoundException(id));
    }

    public Customer removeCustomer(UUID id) {
        return this.customerRepository.findById(id).map(existing -> {
            customerRepository.delete(existing);
            return existing;
        }).orElseThrow(() -> new CustomerNotFoundException(id));
    }

}

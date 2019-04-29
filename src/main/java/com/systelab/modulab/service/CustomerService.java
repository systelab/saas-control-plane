package com.systelab.modulab.service;

import com.systelab.modulab.config.AWSConfig;
import com.systelab.modulab.event.ApplicationServerCreatedEvent;
import com.systelab.modulab.exception.CustomerNotFoundException;
import com.systelab.modulab.model.customer.Customer;
import com.systelab.modulab.repository.CustomerRepository;
import com.systelab.modulab.service.aws.AMI;
import com.systelab.modulab.service.aws.EC2Service;
import com.systelab.modulab.service.aws.RDSService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
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
    private final RDSService rdsService;
    private final AWSConfig awsConfig;
    private final ApplicationEventPublisher applicationEventPublisher;


    @Autowired
    public CustomerService(CustomerRepository customerRepository, EC2Service ec2Service, RDSService rdsService, AWSConfig awsConfig, ApplicationEventPublisher applicationEventPublisher) {
        this.customerRepository = customerRepository;
        this.ec2Service = ec2Service;
        this.rdsService = rdsService;
        this.awsConfig = awsConfig;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    public Page<Customer> getAllCustomers(Pageable pageable) {
        final PageRequest page = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.Direction.ASC, "name");
        return this.customerRepository.findAll(page);
    }

    public Customer getCustomer(UUID customerId) {
        return this.customerRepository.findById(customerId).orElseThrow(() -> new CustomerNotFoundException(customerId));
    }

    public Customer createCustomer(Customer customer) {

        String rdsInstance = this.rdsService.createInstance(customer.getNickname(), this.awsConfig.getVpc());
        customer.setRdsInstance(rdsInstance);
        String ec2Instance = this.ec2Service.createInstance(customer.getNickname(), AMI.AMAZON_LINUX2_AMI);
        customer.setApplicationServerInstance(ec2Instance);
        ApplicationServerCreatedEvent ec2InstanceCreated = new ApplicationServerCreatedEvent(this, ec2Instance);
        applicationEventPublisher.publishEvent(ec2InstanceCreated);
        Customer saved = this.customerRepository.save(customer);
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

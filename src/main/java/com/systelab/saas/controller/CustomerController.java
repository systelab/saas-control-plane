package com.systelab.saas.controller;

import java.net.URI;
import java.util.UUID;

import javax.validation.Valid;

import com.systelab.saas.model.customer.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.systelab.saas.service.CustomerService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.Authorization;

@Api(value = "Customer", description = "API for customer management", tags = { "Customer" })
@RestController()
@CrossOrigin(origins = "*", allowedHeaders = "*", exposedHeaders = "Authorization", allowCredentials = "true")
@RequestMapping(value = "/saas/v1", produces = MediaType.APPLICATION_JSON_VALUE)
public class CustomerController {

    private final CustomerService customerService;

    @Autowired
    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @ApiOperation(value = "Get all Customers", authorizations = { @Authorization(value = "Bearer") })
    @GetMapping("customers")
    public ResponseEntity<Page<Customer>> getAllCustomers(Pageable pageable) {
        return ResponseEntity.ok(this.customerService.getAllCustomers(pageable));
    }

    @ApiOperation(value = "Get Customer", authorizations = { @Authorization(value = "Bearer") })
    @GetMapping("customers/{uid}")
    public ResponseEntity<Customer> getCustomer(@PathVariable("uid") UUID id) {
        return ResponseEntity.ok(this.customerService.getCustomer(id));
    }

    @ApiOperation(value = "Create a Customer", authorizations = { @Authorization(value = "Bearer") })
    @PostMapping("customers/customer")
    public ResponseEntity<Customer> createCustomer(@RequestBody @ApiParam(value = "Customer", required = true) @Valid Customer p) {
        Customer customer = this.customerService.createCustomer(p);
        URI uri = MvcUriComponentsBuilder.fromController(getClass()).path("/customers/{id}").buildAndExpand(customer.getId()).toUri();
        return ResponseEntity.created(uri).body(customer);
    }

    @ApiOperation(value = "Update an existing Customer", authorizations = { @Authorization(value = "Bearer") })
    @PutMapping("customers/{uid}")
    public ResponseEntity<Customer> updateCustomer(@PathVariable("uid") UUID id, @RequestBody @ApiParam(value = "Customer", required = true) @Valid Customer p) {
        Customer customer = this.customerService.updateCustomer(id, p);
        URI selfLink = URI.create(ServletUriComponentsBuilder.fromCurrentRequest().toUriString());
        return ResponseEntity.created(selfLink).body(customer);
    }

    @ApiOperation(value = "Delete a Customer", authorizations = { @Authorization(value = "Bearer") })
    @DeleteMapping("customers/{uid}")
    public ResponseEntity removeCustomer(@PathVariable("uid") UUID id) {
        this.customerService.removeCustomer(id);
        return ResponseEntity.noContent().build();
    }

}
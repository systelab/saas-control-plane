package com.systelab.saas.controller;

import java.net.URI;
import java.util.UUID;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import com.systelab.saas.model.customer.Customer;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.converters.models.PageableAsQueryParam;
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

@Tag(name = "Customer")
@RequiredArgsConstructor
@RestController()
@RequestMapping(value = "/saas/v1", produces = MediaType.APPLICATION_JSON_VALUE)
public class CustomerController {

    private final CustomerService customerService;

    @Operation(description = "Get all Customers")
    @PageableAsQueryParam
    @SecurityRequirement(name = "Authorization")
    @GetMapping("customers")
    public ResponseEntity<Page<Customer>> getAllCustomers(@Parameter(hidden = true) Pageable pageable) {
        return ResponseEntity.ok(this.customerService.getAllCustomers(pageable));
    }

    @Operation(description = "Get Customer")
    @SecurityRequirement(name = "Authorization")
    @GetMapping("customers/{uid}")
    public ResponseEntity<Customer> getCustomer(@PathVariable("uid") UUID id) {
        return ResponseEntity.ok(this.customerService.getCustomer(id));
    }

    @Operation(description = "Create a Customer")
    @SecurityRequirement(name = "Authorization")
    @PostMapping("customers/customer")
    public ResponseEntity<Customer> createCustomer(@RequestBody @Parameter(description= "Customer", required = true) @Valid Customer p) {
        Customer customer = this.customerService.createCustomer(p);
        URI uri = MvcUriComponentsBuilder.fromController(getClass()).path("/customers/{id}").buildAndExpand(customer.getId()).toUri();
        return ResponseEntity.created(uri).body(customer);
    }

    @Operation(description = "Update an existing Customer")
    @SecurityRequirement(name = "Authorization")
    @PutMapping("customers/{uid}")
    public ResponseEntity<Customer> updateCustomer(@PathVariable("uid") UUID id, @RequestBody @Parameter(description = "Customer", required = true) @Valid Customer p) {
        Customer customer = this.customerService.updateCustomer(id, p);
        URI selfLink = URI.create(ServletUriComponentsBuilder.fromCurrentRequest().toUriString());
        return ResponseEntity.created(selfLink).body(customer);
    }

    @Operation(description = "Delete a Customer")
    @SecurityRequirement(name = "Authorization")
    @DeleteMapping("customers/{uid}")
    public ResponseEntity removeCustomer(@PathVariable("uid") UUID id) {
        this.customerService.removeCustomer(id);
        return ResponseEntity.noContent().build();
    }

}
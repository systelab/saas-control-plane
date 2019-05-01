package com.systelab.saas.event;

import com.systelab.saas.model.customer.Customer;
import org.springframework.context.ApplicationEvent;

public class CustomerCreatedEvent extends ApplicationEvent {
    private Customer customer;

    public CustomerCreatedEvent(Object source, Customer customer) {
        super(source);
        this.customer = customer;
    }

    public Customer getCustomer() {
        return customer;
    }
}
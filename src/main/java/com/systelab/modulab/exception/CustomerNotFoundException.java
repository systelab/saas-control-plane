package com.systelab.modulab.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.UUID;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class CustomerNotFoundException extends RuntimeException {

    private final String id;

    public CustomerNotFoundException(UUID id) {
        super("customer-not-found-" + id.toString());
        this.id = id.toString();
    }

    public String getCustomerId() {
        return id;
    }
}
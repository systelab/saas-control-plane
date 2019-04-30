package com.systelab.saas.event;

import org.springframework.context.ApplicationEvent;

public class ApplicationServerCreatedEvent extends ApplicationEvent {
    private String instanceID;

    public ApplicationServerCreatedEvent(Object source, String instanceID) {
        super(source);
        this.instanceID = instanceID;
    }
    public String getInstanceID() {
        return instanceID;
    }
}
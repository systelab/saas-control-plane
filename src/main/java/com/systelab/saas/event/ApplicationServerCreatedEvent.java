package com.systelab.saas.event;

import org.springframework.context.ApplicationEvent;

public class ApplicationServerCreatedEvent extends ApplicationEvent {
    private String instanceId;
    private String rdsInstanceId;

    public ApplicationServerCreatedEvent(Object source, String rdsInstanceId, String instanceId) {
        super(source);
        this.instanceId = instanceId;
        this.rdsInstanceId = rdsInstanceId;
    }

    public String getInstanceId() {
        return instanceId;
    }

    public String getRdsInstanceId() {
        return rdsInstanceId;
    }

}
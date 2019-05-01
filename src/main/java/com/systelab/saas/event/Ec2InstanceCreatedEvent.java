package com.systelab.saas.event;

import org.springframework.context.ApplicationEvent;

public class Ec2InstanceCreatedEvent extends ApplicationEvent {
    private String ec2InstanceId;
    private String rdsInstanceId;

    public Ec2InstanceCreatedEvent(Object source, String rdsInstanceId, String ec2InstanceId) {
        super(source);
        this.ec2InstanceId = ec2InstanceId;
        this.rdsInstanceId = rdsInstanceId;
    }

    public String getEc2InstanceId() {
        return ec2InstanceId;
    }

    public String getRdsInstanceId() {
        return rdsInstanceId;
    }

}
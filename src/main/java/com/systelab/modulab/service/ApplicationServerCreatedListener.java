package com.systelab.modulab.service;

import com.systelab.modulab.config.AWSConfig;
import com.systelab.modulab.event.ApplicationServerCreatedEvent;
import com.systelab.modulab.service.aws.EC2Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class ApplicationServerCreatedListener {

    private final EC2Service ec2Service;
    private final AWSConfig awsConfig;

    @Autowired
    public ApplicationServerCreatedListener(EC2Service ec2Service, AWSConfig awsConfig) {
        this.ec2Service = ec2Service;
        this.awsConfig = awsConfig;
    }

    @Async
    @EventListener
    public void handleServerCreated(ApplicationServerCreatedEvent event) throws InterruptedException {
        System.out.println("Server was created " + event.getInstanceID());
        System.out.println("Wait until everything is up and run the scripts");
        while (!ec2Service.isInstanceRunning(event.getInstanceID())) {
            Thread.sleep(100);
        }
        String commandID = ec2Service.runCommand(event.getInstanceID(), this.awsConfig.getCommand());
        while (!ec2Service.isCommandInvocationSuccess(event.getInstanceID(),commandID)) {
            Thread.sleep(100);
        }
        System.out.println(ec2Service.getCommandInvocationOutput(event.getInstanceID(), commandID));
        System.out.println("Send e-mail to the customer as everything is available");
    }
}

package com.systelab.saas.service;

import com.systelab.saas.config.AWSConfig;
import com.systelab.saas.event.ApplicationServerCreatedEvent;
import com.systelab.saas.service.aws.EC2Service;
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
        while (!ec2Service.isInstanceRunning(event.getInstanceID())) {
            Thread.sleep(500);
        }
        while (!ec2Service.isInstanceCheckPassed(event.getInstanceID())) {
            Thread.sleep(500);
        }

        String commandID = ec2Service.runCommand(event.getInstanceID(), this.awsConfig.getCommand());
        System.out.println("Commmand "+commandID+" created.");
        Thread.sleep(500);
        while (!ec2Service.isCommandInvocationSuccess(event.getInstanceID(),commandID)) {
            System.out.println("Wait for the command to be executed...");
            Thread.sleep(500);
        }
        System.out.println(ec2Service.getCommandInvocationOutput(event.getInstanceID(), commandID));
        System.out.println("Send e-mail to the customer as everything is available");
    }
}

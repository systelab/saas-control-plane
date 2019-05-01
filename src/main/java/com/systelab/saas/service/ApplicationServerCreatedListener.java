package com.systelab.saas.service;

import com.systelab.saas.config.AWSConfig;
import com.systelab.saas.event.ApplicationServerCreatedEvent;
import com.systelab.saas.service.aws.EC2Service;
import com.systelab.saas.service.aws.RDSService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class ApplicationServerCreatedListener {

    private final EC2Service ec2Service;
    private final RDSService rdsService;
    private final AWSConfig awsConfig;

    @Autowired
    public ApplicationServerCreatedListener(EC2Service ec2Service, RDSService rdsService, AWSConfig awsConfig) {
        this.ec2Service = ec2Service;
        this.rdsService = rdsService;
        this.awsConfig = awsConfig;
    }

    @Async
    @EventListener
    public void handleServerCreated(ApplicationServerCreatedEvent event) throws InterruptedException {
        System.out.println("Server was created " + event.getInstanceId());
        while (!ec2Service.isInstanceAvailable(event.getInstanceId())) {
            System.out.println("EC2 is in state: " + ec2Service.getInstanceState(event.getInstanceId()));
            Thread.sleep(500);
        }

        String commandID = ec2Service.runCommand(event.getInstanceId(), this.awsConfig.getCommand());
        System.out.println("Commmand " + commandID + " created.");
        Thread.sleep(500);
        while (!ec2Service.isCommandInvocationSuccess(event.getInstanceId(), commandID)) {
            System.out.println("Wait for the command to be executed...");
            Thread.sleep(500);
        }
        System.out.println(ec2Service.getCommandInvocationOutput(event.getInstanceId(), commandID));

        while (!rdsService.isInstanceAvailable(event.getRdsInstanceId())) {
            System.out.println("RDS is in state: " + rdsService.getInstanceState(event.getRdsInstanceId()));
            Thread.sleep(500);
        }

        System.out.println("Send e-mail to the customer as everything is available");
    }
}

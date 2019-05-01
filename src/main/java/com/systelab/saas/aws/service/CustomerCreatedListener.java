package com.systelab.saas.aws.service;

import com.systelab.saas.aws.config.AWSConfig;
import com.systelab.saas.aws.sdk.AMI;
import com.systelab.saas.aws.sdk.EC2Service;
import com.systelab.saas.aws.sdk.RDSService;
import com.systelab.saas.event.CustomerCreatedEvent;
import com.systelab.saas.model.customer.Customer;
import com.systelab.saas.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class CustomerCreatedListener {

    private final EC2Service ec2Service;
    private final RDSService rdsService;
    private final AWSConfig awsConfig;
    private final CustomerService customerService;

    @Autowired
    public CustomerCreatedListener(EC2Service ec2Service, RDSService rdsService, AWSConfig awsConfig, CustomerService customerService) {
        this.ec2Service = ec2Service;
        this.rdsService = rdsService;
        this.awsConfig = awsConfig;
        this.customerService = customerService;
    }

    @Async
    @EventListener
    public void handleCustomerCreated(CustomerCreatedEvent event) throws InterruptedException {
        Customer customer = event.getCustomer();
        String rdsInstance = createRDSInstance(customer);
        String ec2Instance = createEC2Instance(customer);
        waitForTheEC2InstanceToBeInitialized(ec2Instance);
        runInitializationScript(ec2Instance);
        waitForTheRDSInstanceToBeInitialized(rdsInstance);
        System.out.println("Send e-mail to the customer applicationServer everything is available");
    }

    private String createRDSInstance(Customer customer) {
        if (customer.getDatabaseServer().getInstanceId() == null) {
            String rdsInstance = this.rdsService.createInstance(customer.getNickname(), this.awsConfig.getVpc());
            customer.getDatabaseServer().setInstanceId(rdsInstance);
            this.customerService.updateCustomerDatabase(customer.getId(), customer);
            System.out.println("RDS instance was created: " + rdsInstance);
            return rdsInstance;
        } else {
            return customer.getDatabaseServer().getInstanceId();
        }
    }

    private String createEC2Instance(Customer customer) {
        if (customer.getApplicationServer().getInstanceId() == null) {
            String ec2Instance = this.ec2Service.createInstance(customer.getNickname(), AMI.AMAZON_LINUX2_AMI, this.awsConfig.getKeyPairName(), this.awsConfig.getEc2SecurityGroup());
            customer.getApplicationServer().setInstanceId(ec2Instance);
            this.customerService.updateCustomerApplicationServer(customer.getId(), customer);
            System.out.println("EC2 instance was created: " + ec2Instance);
            return ec2Instance;
        } else {
            return customer.getApplicationServer().getInstanceId();
        }
    }

    private void runInitializationScript(String ec2Instance) throws InterruptedException {
        String commandID = ec2Service.runCommand(ec2Instance, this.awsConfig.getCommand());
        System.out.println("Commmand " + commandID + " created.");
        Thread.sleep(500);
        while (!ec2Service.isCommandInvocationSuccess(ec2Instance, commandID)) {
            System.out.println("Wait for the command to be executed...");
            Thread.sleep(500);
        }
        System.out.println(ec2Service.getCommandInvocationOutput(ec2Instance, commandID));
    }

    private void waitForTheEC2InstanceToBeInitialized(String ec2Instance) throws InterruptedException {
        while (!ec2Service.isInstanceAvailable(ec2Instance)) {
            System.out.println("EC2 is in state: " + ec2Service.getInstanceState(ec2Instance));
            Thread.sleep(500);
        }
    }

    private void waitForTheRDSInstanceToBeInitialized(String rdsInstance) throws InterruptedException {
        while (!rdsService.isInstanceAvailable(rdsInstance)) {
            System.out.println("RDS is in state: " + rdsService.getInstanceState(rdsInstance));
            Thread.sleep(500);
        }
    }
}

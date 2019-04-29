package com.systelab.modulab.service.aws;

import org.springframework.stereotype.Service;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.Tag;
import software.amazon.awssdk.services.ec2.model.*;
import software.amazon.awssdk.services.ssm.SsmClient;
import software.amazon.awssdk.services.ssm.model.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class EC2Service {
    private Region region = Region.EU_CENTRAL_1;
    private Ec2Client ec2 = Ec2Client.builder().region(region).build();
    private SsmClient ssm = SsmClient.builder().region(region).build();

    private void printInstance(Instance instance) {
        System.out.printf(
                "Found reservation with id %s, " +
                        "AMI %s, " +
                        "type %s, " +
                        "state %s " +
                        "and monitoring state %s\n",
                instance.instanceId(),
                instance.imageId(),
                instance.instanceType(),
                instance.state().name(),
                instance.monitoring().state());
    }

    private void printReservation(Reservation reservation) {
        reservation.instances().forEach(this::printInstance);
    }

    public void getInstances() {
        String nextToken = null;
        do {
            DescribeInstancesRequest request = DescribeInstancesRequest.builder().maxResults(6).nextToken(nextToken).build();
            DescribeInstancesResponse response = ec2.describeInstances(request);
            response.reservations().forEach(this::printReservation);
            nextToken = response.nextToken();
        } while (nextToken != null);
    }

    public String createInstance(String name, AMI ami) throws Ec2Exception {
        return this.createInstance(name, ami, InstanceType.T2_SMALL);
    }

    public String createInstance(String name, AMI ami, InstanceType type) throws Ec2Exception {
        IamInstanceProfileSpecification spec = IamInstanceProfileSpecification.builder().name("EC2ReadS3AserraModulab").build();

        RunInstancesRequest request = RunInstancesRequest.builder()
                .imageId(ami.toString())
                .instanceType(type)
                .iamInstanceProfile(spec)
                .maxCount(1)
                .minCount(1)
                .build();

        RunInstancesResponse response = ec2.runInstances(request);

        String instanceId = response.instances().get(0).instanceId();

        addTagsToInstance("Name", name, instanceId);

        System.out.printf("Successfully started EC2Service instance %s based on AMI %s", instanceId, ami.toString());
        return instanceId;
    }

    public boolean isInstanceRunning(String instanceId) throws Ec2Exception {
        DescribeInstancesRequest request = DescribeInstancesRequest.builder().instanceIds(instanceId).build();

        DescribeInstancesResponse response = ec2.describeInstances(request);
        InstanceStateName status=response.reservations().get(0).instances().get(0).state().name();
        return status==InstanceStateName.RUNNING;
    }

    public boolean isInstanceCheckPassed(String instanceId) throws Ec2Exception {
        DescribeInstanceStatusRequest request = DescribeInstanceStatusRequest.builder().instanceIds(instanceId).build();
        DescribeInstanceStatusResponse response = ec2.describeInstanceStatus(request);
        return response.instanceStatuses().stream().allMatch(is -> is.systemStatus().status().name().equals("OK"));
    }

    public void startInstance(String instanceId) {
        StartInstancesRequest request = StartInstancesRequest.builder()
                .instanceIds(instanceId).build();
        ec2.startInstances(request);
    }

    public void rebootInstance(String instanceId) {
        RebootInstancesRequest request = RebootInstancesRequest.builder()
                .instanceIds(instanceId).build();
        RebootInstancesResponse response = ec2.rebootInstances(request);
    }

    public void stopInstance(String instanceId) {
        StopInstancesRequest request = StopInstancesRequest.builder()
                .instanceIds(instanceId).build();
        ec2.stopInstances(request);
    }

    // Remember to add the policy AmazonSSMFullAccess to the IAM role associated to the instance
    public String runCommand(String instanceId, String script) {

        List<String> value = new ArrayList();
        value.add(script);

        Map<String, List<String>> commands = new HashMap();
        commands.put("commands", value);

        SendCommandRequest request = SendCommandRequest.builder().instanceIds(instanceId).documentName("AWS-RunShellScript")
                .parameters(commands).build();
        SendCommandResponse response = ssm.sendCommand(request);
        return response.command().commandId();
    }

    public boolean isCommandInvocationSuccess(String instanceId, String commandId) {
        GetCommandInvocationRequest request = GetCommandInvocationRequest.builder().instanceId(instanceId).commandId(commandId).build();
        GetCommandInvocationResponse response = ssm.getCommandInvocation(request);
        return response.status() == CommandInvocationStatus.SUCCESS;
    }

    public String getCommandInvocationOutput(String instanceId, String commandId) {
        GetCommandInvocationRequest request = GetCommandInvocationRequest.builder().instanceId(instanceId).commandId(commandId).build();
        GetCommandInvocationResponse response = ssm.getCommandInvocation(request);
        return response.standardOutputContent();
    }

    private void addTagsToInstance(String key, String value, String... instances) {
        Tag tag = Tag.builder()
                .key(key)
                .value(value)
                .build();

        CreateTagsRequest tagsRequest = CreateTagsRequest.builder()
                .resources(instances)
                .tags(tag)
                .build();
        ec2.createTags(tagsRequest);
    }

}

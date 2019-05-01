package com.systelab.saas.aws.service;

import org.springframework.stereotype.Service;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.DescribeVpcsRequest;
import software.amazon.awssdk.services.ec2.model.DescribeVpcsResponse;
import software.amazon.awssdk.services.ec2.model.Vpc;

@Service
public class VPCService {
    private Region region = Region.EU_CENTRAL_1;
    private Ec2Client ec2 = Ec2Client.builder().region(region).build();

    private void printVpc(Vpc vpc) {
        System.out.printf(
                "Found vpc with id %s, " +
                        "state %s, " +
                        "cidr %s, " +
                        "dhcp options %s, " +
                        "and is default %s\n",
                vpc.vpcId(),
                vpc.state().toString(),
                vpc.cidrBlock(),
                vpc.dhcpOptionsId(),
                vpc.isDefault());
    }

    public void getVPCs() {
        String nextToken = null;
        do {
            DescribeVpcsRequest request = DescribeVpcsRequest.builder().maxResults(6).nextToken(nextToken).build();
            DescribeVpcsResponse response = ec2.describeVpcs(request);
            response.vpcs().forEach(this::printVpc);
            nextToken = response.nextToken();
        } while (nextToken != null);
    }
}

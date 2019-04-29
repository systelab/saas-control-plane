package com.systelab.modulab.service.aws;

import org.springframework.stereotype.Service;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.*;
import software.amazon.awssdk.services.rds.RdsClient;
import software.amazon.awssdk.services.rds.model.*;

import java.util.Arrays;

@Service
public class RDSService {
    private Region region = Region.EU_CENTRAL_1;
    private RdsClient rds = RdsClient.builder().region(region).build();
    private Ec2Client ec2 = Ec2Client.builder().region(region).build();

    public void printInstance(DBInstance dbInstance) {
        System.out.println(dbInstance.dbName());
        System.out.println(dbInstance.engine());
        System.out.println(dbInstance.engineVersion());
        System.out.println(dbInstance.licenseModel());
        System.out.println(dbInstance.endpoint().address());
        System.out.println(dbInstance.endpoint().port());
        System.out.println(dbInstance.autoMinorVersionUpgrade());
        System.out.println(dbInstance.dbInstanceClass());
        System.out.println(dbInstance.allocatedStorage());
        System.out.println(dbInstance.dbInstanceIdentifier());
        System.out.println(dbInstance.backupRetentionPeriod());

        dbInstance.dbParameterGroups().forEach((info) -> System.out.println(info));
        dbInstance.dbSecurityGroups().forEach((info) -> System.out.println(info));
        dbInstance.statusInfos().forEach((info) -> System.out.println(info.status()));
    }

    public void getInstances() {
        DescribeDbInstancesRequest request = DescribeDbInstancesRequest.builder().maxRecords(100).build();
        DescribeDbInstancesResponse response = rds.describeDBInstances(request);
        response.dbInstances().forEach(this::printInstance);
    }

    public void getInstance(String instanceId) {
        DescribeDbInstancesRequest request = DescribeDbInstancesRequest.builder().dbInstanceIdentifier(instanceId).build();
        DescribeDbInstancesResponse response = rds.describeDBInstances(request);
        response.dbInstances().forEach(this::printInstance);
    }

    public void stopInstance(String instanceId) {
        StopDbInstanceRequest request = StopDbInstanceRequest.builder()
                .dbInstanceIdentifier(instanceId).build();
        rds.stopDBInstance(request);
    }

    public void startInstance(String instanceId) {
        StartDbInstanceRequest request = StartDbInstanceRequest.builder()
                .dbInstanceIdentifier(instanceId).build();
        rds.startDBInstance(request);
    }

    public void deleteInstance(String instanceId) {
        DeleteDbInstanceRequest request = DeleteDbInstanceRequest.builder()
                .dbInstanceIdentifier(instanceId).skipFinalSnapshot(true).build();
        rds.deleteDBInstance(request);
    }

    public String createSecurityGroup(String vpcID) {
        CreateSecurityGroupRequest request = CreateSecurityGroupRequest.builder()
                .groupName("temp3-rds-security-group")
                .description("Created from the RDS").vpcId(vpcID).build();

        CreateSecurityGroupResponse response = ec2.createSecurityGroup(request);
        addInboundRules(response.groupId());
     //   addOutboundRules(response.groupId());
        return response.groupId();
    }

    public void addInboundRules(String groupID) {

        IpRange ipRange1 = IpRange.builder().cidrIp("172.31.0.0/16").build();
        IpRange ipRange2 = IpRange.builder().cidrIp("10.0.0.0/16").build();
        IpRange ipRange3 = IpRange.builder().cidrIp("213.9.182.105/32").build();

        IpPermission ipPermission = IpPermission.builder().ipRanges(Arrays.asList(new IpRange[]{ipRange1, ipRange2, ipRange3}))
                .ipProtocol("tcp")
                .fromPort(1521)
                .toPort(1521).build();
        AuthorizeSecurityGroupIngressRequest request =
                AuthorizeSecurityGroupIngressRequest.builder().groupId(groupID).ipPermissions(ipPermission).build();
        ec2.authorizeSecurityGroupIngress(request);
    }

    public void addOutboundRules(String groupID) {

        IpRange ipRange1 = IpRange.builder().cidrIp("0.0.0.0/0").build();

        IpPermission ipPermission = IpPermission.builder().ipRanges(Arrays.asList(new IpRange[]{ipRange1})).ipProtocol("-1").fromPort(-1).toPort(-1).build();
        AuthorizeSecurityGroupEgressRequest request =
                AuthorizeSecurityGroupEgressRequest.builder().groupId(groupID).ipPermissions(ipPermission).build();
        ec2.authorizeSecurityGroupEgress(request);
    }


    public String createInstance(String name, String vpcID) {
/*
            CreateDbParameterGroupRequest request=CreateDbParameterGroupRequest.builder()
                    .dbParameterGroupName(groupName)
                    .description(description)
                    .dbParameterGroupFamily(family).build();

            rds.createDBParameterGroup(request);


            Collection<Parameter> parameters = new ArrayList<Parameter>();
            Parameter.builder().parameterName().parameterValue().applyMethod()))
            parameters.add( new Parameter()
                    .withParameterName(DbParameterName1)
                    .withParameterValue(DbParameterValue1)
                    .withApplyMethod(DbParameterApplyMethod1));
            parameters.add( new Parameter()
                    .withParameterName(DbParameterName2)
                    .withParameterValue(DbParameterValue2)
                    .withApplyMethod(DbParameterApplyMethod2));


            rds.modifyDBParameterGroup( new ModifyDbParameterGroupRequest().withDbParameterGroupName(DbParameterGroupName).withParameters(parameters));
*/

        String securityGroup= createSecurityGroup(vpcID);
        CreateDbInstanceRequest request2 = CreateDbInstanceRequest.builder()
                .dbInstanceIdentifier(name)
                .engine("oracle-se2")
                .engineVersion("12.2.0.1.ru-2019-01.rur-2019-01.r1")
                .licenseModel("license-included")
                .autoMinorVersionUpgrade(false)
                .dbInstanceClass("db.t2.small")
                .multiAZ(false)
                .allocatedStorage(20)
                .dbName("MG2")
                .masterUsername("username")
                .masterUserPassword("password")
                .vpcSecurityGroupIds(securityGroup)
                .port(1521)
                .backupRetentionPeriod(2)
                .publiclyAccessible(false)
                .dbParameterGroupName("default.oracle-se2-12.2").build();

        CreateDbInstanceResponse response2 = rds.createDBInstance(request2);
        String instanceId = response2.dbInstance().dbInstanceIdentifier();
        return instanceId;
    }
}

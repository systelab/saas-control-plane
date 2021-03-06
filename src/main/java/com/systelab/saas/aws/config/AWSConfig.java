package com.systelab.saas.aws.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "aws")
@Getter
@Setter
public class AWSConfig {

    private String vpc;
    private String command;
    private String keyPairName;
    private String ec2SecurityGroup;

}
package com.systelab.modulab.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.io.Serializable;

@Configuration
@ConfigurationProperties(prefix = "aws")
@Getter
@Setter
public class AWSConfig implements Serializable {

    private String vpc;
    private String command;

}
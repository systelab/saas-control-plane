package com.systelab.modulab.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.envers.repository.support.EnversRevisionRepositoryFactoryBean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaAuditing
@EnableJpaRepositories(basePackages = {"com.systelab.modulab.repository"},
        repositoryFactoryBeanClass = EnversRevisionRepositoryFactoryBean.class)
public class RepositoryConfig {
}
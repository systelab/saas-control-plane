package com.systelab.saas.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.envers.repository.support.EnversRevisionRepositoryFactoryBean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaAuditing
@EnableJpaRepositories(basePackages = {"com.systelab.saas.repository"},
        repositoryFactoryBeanClass = EnversRevisionRepositoryFactoryBean.class)
public class RepositoryConfig {
}
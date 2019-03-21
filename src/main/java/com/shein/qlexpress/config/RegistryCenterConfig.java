package com.shein.qlexpress.config;

import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperConfiguration;
import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperRegistryCenter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Jumping.Li
 * @date 2018-12-04 15:41
 */
@Configuration
public class RegistryCenterConfig {
    @Value("${elaticjob.zookeeper.server-lists}")
    private String serverList;

    @Value("${elaticjob.zookeeper.namespace}")
    private String namespace;

    @Bean(initMethod = "init")
    public ZookeeperRegistryCenter zookeeperRegistryCenter(ZookeeperConfiguration configuration){
        return new ZookeeperRegistryCenter(configuration);
    }

    @Bean
    public ZookeeperConfiguration zookeeperConfiguration(){
        ZookeeperConfiguration configuration = new ZookeeperConfiguration(serverList,namespace);
        configuration.setBaseSleepTimeMilliseconds(1000);
        configuration.setMaxSleepTimeMilliseconds(3000);
        configuration.setMaxRetries(3);
        return configuration;
    }
}

package com.shein.qlexpress.config;

import com.dangdang.ddframe.job.api.simple.SimpleJob;
import com.dangdang.ddframe.job.config.JobCoreConfiguration;
import com.dangdang.ddframe.job.config.simple.SimpleJobConfiguration;
import com.dangdang.ddframe.job.lite.api.JobScheduler;
import com.dangdang.ddframe.job.lite.config.LiteJobConfiguration;
import com.dangdang.ddframe.job.lite.lifecycle.api.JobAPIFactory;
import com.dangdang.ddframe.job.lite.lifecycle.api.JobOperateAPI;
import com.dangdang.ddframe.job.lite.spring.api.SpringJobScheduler;
import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperRegistryCenter;
import com.google.common.base.Optional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import javax.annotation.Resource;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author Jumping.Li
 * @date 2018-12-04 17:11
 */
@Configuration
public class JobConfig {

    @Value("${elastic-job.sharding.total.count:2}")
    private Integer shardingTotalCount;
    @Value("${elaticjob.zookeeper.server-lists}")
    private String serverList;

    @Value("${elaticjob.zookeeper.namespace}")
    private String namespace;
    @Resource
    private ZookeeperRegistryCenter regCenter;

    @Resource(name = "monthJob")
    private SimpleJob monthJob;

    @Resource(name = "quarterJob")
    private SimpleJob quarterJob;

    @Bean(initMethod = "init")
    public JobScheduler myJobDemo2Scheduler() {
        return new SpringJobScheduler(monthJob, regCenter,
                getLiteJobConfiguration(monthJob.getClass(), "0 0/5 * * * ?", 1, "0=A"));
    }

//    @Bean(initMethod = "init")
    public JobScheduler myJobDemoScheduler() {
        return new SpringJobScheduler(quarterJob, regCenter,
                getLiteJobConfiguration(quarterJob.getClass(), "0 0/5 * * * ?", shardingTotalCount, "0=C,1=D"));
    }

    private LiteJobConfiguration getLiteJobConfiguration(final Class<? extends SimpleJob> jobClass,
                                                         final String cron,
                                                         final int shardingTotalCount,
                                                         final String shardingItemParameters) {
        return LiteJobConfiguration.newBuilder(
                new SimpleJobConfiguration(
                        JobCoreConfiguration.newBuilder(jobClass.getSimpleName(), cron, shardingTotalCount)
                                .shardingItemParameters(shardingItemParameters)
                                .jobParameter("Hello World").build(), jobClass.getCanonicalName()))
                .overwrite(true)
//                .disabled(true)//作业是否启动时禁止
//                .jobShardingStrategyClass()
                .build();

    }

    /**
     * 动态添加任务
     *
     * @param jobClass
     * @param cron
     * @param shardingTotalCount
     * @param shardingItemParameters
     */
    public void addSimpleJobScheduler(final Class<? extends SimpleJob> jobClass,
                                      final String cron,
                                      final int shardingTotalCount,
                                      final String shardingItemParameters) {
        JobCoreConfiguration coreConfig = JobCoreConfiguration.newBuilder(jobClass.getName(), cron, shardingTotalCount).
                shardingItemParameters(shardingItemParameters).build();
        SimpleJobConfiguration simpleJobConfig = new SimpleJobConfiguration(coreConfig, jobClass.getCanonicalName());
        JobScheduler jobScheduler = new JobScheduler(regCenter, LiteJobConfiguration.newBuilder(simpleJobConfig).disabled(true).build());
        jobScheduler.init();
    }

    public void enableJob(String jobName){
        JobOperateAPI jobAPIService= JobAPIFactory.createJobOperateAPI(serverList, namespace, Optional.fromNullable(null));
        jobAPIService.enable(Optional.fromNullable(jobName), Optional.absent());
        jobAPIService.trigger(Optional.fromNullable(jobName), Optional.absent());
    }

    public void disableJob(String jobName){
        JobOperateAPI jobAPIService= JobAPIFactory.createJobOperateAPI(serverList, namespace, Optional.fromNullable(null));
        jobAPIService.disable(Optional.fromNullable(jobName), Optional.absent());
    }
    @Bean
    public Executor evaluationExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        int processorCount = Runtime.getRuntime().availableProcessors() + 1;
        executor.setCorePoolSize(processorCount);
        executor.setMaxPoolSize(processorCount * 2);
        executor.setQueueCapacity(100);
        executor.setKeepAliveSeconds(60);
        executor.setAllowCoreThreadTimeOut(true);
        executor.setThreadNamePrefix("srm-evaluationTaskExecutor-");
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return executor;
    }
}

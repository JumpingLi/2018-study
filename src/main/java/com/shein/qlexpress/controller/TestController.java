package com.shein.qlexpress.controller;

import com.dangdang.ddframe.job.lite.lifecycle.api.JobAPIFactory;
import com.dangdang.ddframe.job.lite.lifecycle.api.JobOperateAPI;
import com.google.common.base.Optional;
import com.shein.qlexpress.config.JobConfig;
import com.shein.qlexpress.job.MonthJob;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author Jumping.Li
 * @date 2018-12-05 13:32
 */
//@RestController
public class TestController {
    @Resource
    private JobConfig elasticJobConfig;

    @Value("${elaticjob.zookeeper.server-lists}")
    private String serverList;

    @Value("${elaticjob.zookeeper.namespace}")
    private String namespace;

    @RequestMapping("/addJob")
    public String addJob() {
        int shardingTotalCount = 2;
        elasticJobConfig.addSimpleJobScheduler(MonthJob.class, "0/5 * * * * ?", shardingTotalCount, "0=C,1=D");
        return "ok";
    }

    @RequestMapping("/disableJob")
    public String disable(@RequestParam(name = "jobName") String jobName){
        JobOperateAPI jobAPIService= JobAPIFactory.createJobOperateAPI(serverList, namespace, Optional.fromNullable(null));
        jobAPIService.disable(Optional.fromNullable(jobName), Optional.absent());
        return "ok";
    }

    @RequestMapping("/enableJob")
    public String enable(@RequestParam(name = "jobName") String jobName){
        JobOperateAPI jobAPIService= JobAPIFactory.createJobOperateAPI(serverList, namespace, Optional.fromNullable(null));
        jobAPIService.enable(Optional.fromNullable(jobName), Optional.absent());
        jobAPIService.trigger(Optional.fromNullable(jobName), Optional.absent());
        return "ok";
    }

    @RequestMapping("/triggerJob")
    public String trigger(@RequestParam(name = "jobName") String jobName){
        JobOperateAPI jobAPIService= JobAPIFactory.createJobOperateAPI(serverList, namespace, Optional.fromNullable(null));
        jobAPIService.trigger(Optional.fromNullable(jobName), Optional.absent());
        return "ok";
    }
}

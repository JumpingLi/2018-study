package com.shein.qlexpress.job;

import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import com.shein.qlexpress.config.JobConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author Jumping.Li
 * @date 2018-12-04 17:02
 */
//@ElasticSimpleJob(cron = "0/5 * * * * ?", jobName = "test123", shardingTotalCount = 2, jobParameter = "测试参数", shardingItemParameters = "0=A,1=B")
//@Component(value = "monthJob")
@Slf4j
public class MonthJob implements SimpleJob {


    @Resource
    private JobConfig elasticJobConfig;

    @Override
    public void execute(ShardingContext shardingContext) {
        log.info("任务开始。。。。。");
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        /**
         * 此处
         */
//        Thread myThread = new Thread(() -> {
//            log.info("开始异步线程。。。。。");
//            try {
//                Thread.sleep(10000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            log.info("结束异步线程。。。。。");
//        });
//        myThread.start();
        log.info(String.format("------Thread ID: %s, 任务总片数: %s, " +
                        "当前分片项: %s.当前参数: %s,"+
                        "当前任务名称: %s.当前任务参数: %s",
                Thread.currentThread().getId(),
                shardingContext.getShardingTotalCount(),
                shardingContext.getShardingItem(),
                shardingContext.getShardingParameter(),
                shardingContext.getJobName(),
                shardingContext.getJobParameter()

        ));
        try {
            Thread.sleep(8000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        log.info("任务结束。。。。。");
//        elasticJobConfig.disableJob("monthJob");
    }
}

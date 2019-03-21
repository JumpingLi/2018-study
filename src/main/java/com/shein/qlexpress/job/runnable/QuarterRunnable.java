package com.shein.qlexpress.job.runnable;

import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @author Jumping.Li
 * @date 2018-12-11 16:20
 */
@Slf4j
public class QuarterRunnable implements Runnable {

    private List<?> dateList;

    public QuarterRunnable(List<?> dateList) {
        this.dateList = dateList;
    }

    @Override
    public void run() {
        // 执行计算开始：executedBatchCount += 1
        dateList.forEach(item -> log.info("我是处理季度考核的runnable,这是处理第{}页数据",item));
        // 成功完成计算：successBatchCount += 1
        // finally{} 当 totalBatchCount == executeBatchCount,去处理任务完成的状态更新或者通知,再将Job失效,以待下一次业务发起时开启调度
    }
}

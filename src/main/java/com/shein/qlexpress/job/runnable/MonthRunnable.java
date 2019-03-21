package com.shein.qlexpress.job.runnable;

/**
 * @author Jumping.Li
 * @date 2018-12-11 16:20
 */
public class MonthRunnable implements Runnable {
    @Override
    public void run() {
        // 执行计算开始：executeBatchCount += 1
        System.out.println("我是处理月度考核的runnable");
        // 成功完成计算：successBatchCount += 1
    }
}

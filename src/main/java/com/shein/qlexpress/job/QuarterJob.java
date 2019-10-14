package com.shein.qlexpress.job;

import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import com.shein.qlexpress.job.runnable.QuarterRunnable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executor;

/**
 * @author Jumping.Li
 * @date 2018-12-04 17:02
 */
//@ElasticSimpleJob(cron = "0/5 * * * * ?", jobName = "test123", shardingTotalCount = 2, jobParameter = "测试参数", shardingItemParameters = "0=A,1=B")
//@Component(value = "quarterJob")
@Slf4j
public class QuarterJob implements SimpleJob {

    @Resource(name = "evaluationExecutor")
    private Executor evaluationExecutor;

    /**
     * 每页(每批次)处理数据
     */
    @Value("${elaticjob.page.size:50}")
    private Integer pageSize;

    @Override
    public void execute(ShardingContext shardingContext) {
        // todo 业务侧用户发起时,初始化以下预备数据
        /**
         * 例：
         * 1、月度考核Task主数据, 增加非业务字段：totalBatchCount,executedBatchCount,successBatchCount
         * 2、task的明细,以某个供应商为粒度的考核明细数据
         * 3、开启获取BI数据的job,更新上一步初始化明细数据的考核指标得分项,完成后进入下一步
         * 4、enable开启计算Job,trigger立即触发计算Job执行(项目启动时会初始化job,但设置"job启动时禁止",在业务需要时enable生效调度)
         * 5、建议job的调度周期设置远大于任务的执行时间,job将在任务完成时disable失效job
         */

        /**
         * 获取BI数据作业：分片数据在分片的节点上再分页分批串行处理
         */

        /**
         * 计算作业：分片数据在分片的节点上再分页分批并行处理
         */
        // 分片总数
        int shardingTotalCount = shardingContext.getShardingTotalCount();
        // 当前分片项
        int currentShardingItem = shardingContext.getShardingItem();
        // todo 初始化的待处理数据总数-从数据库预备数据查出
        int initializedDataTotalCount = 1000;
        // 总页数(总批次)
        int totalPageCount = initializedDataTotalCount % pageSize == 0 ? initializedDataTotalCount / pageSize : initializedDataTotalCount / pageSize + 1;
        // 平均分给每片的处理页数
        int pageCount = totalPageCount / shardingTotalCount;
        // 余下的页数
        int mod = totalPageCount % shardingTotalCount;
        // 当前分片处理的开始页码
        int pageNum = 1;
        pageNum += currentShardingItem * pageCount;
        // 如果是最后一片加上余下的页数
        if (currentShardingItem == shardingTotalCount - 1){
            pageCount += mod;
        }
        // todo Task任务写入 totalBatchCount = totalPageCount + mod;
        // 从数据库分页提取待处理的数据,交给线程池并行处理
        for (int i = pageNum; i < pageNum + pageCount; i++) {
            // todo 内部写入该批次的执行记录,用于记录任务执行过程中的有关数据例如线程id,非业务表
            // mapper.findListByPage(pageNum,pageSize)
            List<?> dateList = Collections.singletonList(i);
            Runnable runnable = new QuarterRunnable(dateList);
            evaluationExecutor.execute(runnable);
        }

//        log.info(String.format("------Thread ID: %s, 任务总片数: %s, " +
//                        "当前分片项: %s.当前参数: %s," +
//                        "当前任务名称: %s.当前任务参数: %s",
//                Thread.currentThread().getId(),
//                shardingContext.getShardingTotalCount(),
//                shardingContext.getShardingItem(),
//                shardingContext.getShardingParameter(),
//                shardingContext.getJobName(),
//                shardingContext.getJobParameter()
//
//        ));
    }
}

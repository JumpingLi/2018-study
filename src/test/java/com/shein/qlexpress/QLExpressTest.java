package com.shein.qlexpress;

import com.google.common.collect.ImmutableMap;
import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressRunner;
import com.ql.util.express.IExpressContext;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Jumping.Li
 * @date 2018-11-20 17:51
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class QLExpressTest{
    /**
     * 计算引擎
     */
    private ExpressRunner runner = new ExpressRunner();

    /**
     * 判断符号解释器
     *
     * @throws Exception
     */
    private void initial() throws Exception {
        runner.addOperatorWithAlias("小于", "<", "$1 小于 $2 不满足期望");
        runner.addOperatorWithAlias("大于", ">", "$1 大于 $2 不满足期望");
        runner.addOperatorWithAlias("等于", "==", "$1 等于 $2 不满足期望");
    }

    /**
     * 短路逻辑
     *
     * @throws Exception
     */
    @Test
    public void testShortCircuit() throws Exception {
        runner.setShortCircuit(true);
        IExpressContext<String, Object> expressContext = new DefaultContext<>();
        expressContext.put("违规天数", 100);
        expressContext.put("虚假交易扣分", 11);
        expressContext.put("VIP", false);
        List<String> errorInfo = new ArrayList<>();
        initial();
        String expression = "2 小于 1 and (违规天数 小于 90 or 虚假交易扣分 小于 12)";
        boolean result = calculateLogicTest(expression, expressContext, errorInfo);
        showErrorInfo(result, errorInfo);
    }

    /**
     * 非短路逻辑
     *
     * @throws Exception
     */
    @Test
    public void testNoShortCircuit() throws Exception {
        runner.setShortCircuit(false);
        IExpressContext<String, Object> expressContext = new DefaultContext<>();
        expressContext.put("违规天数", 100);
        expressContext.put("虚假交易扣分", 13);
        expressContext.put("VIP", false);
        List<String> errorInfo = new ArrayList<>();
        initial();
        String expression = "2 等于 1 and (违规天数 小于 90 or 虚假交易扣分 小于 12) and VIP 等于 true";
        boolean result = calculateLogicTest(expression, expressContext, errorInfo);
        showErrorInfo(result, errorInfo);
    }

    private boolean calculateLogicTest(String expression, IExpressContext<String, Object> expressContext, List<String> errorInfo) throws Exception {
        return (Boolean) runner.execute(expression, expressContext, errorInfo, true, false);
    }

    private void showErrorInfo(boolean result, List<String> errorList) {
        if (result) {
            System.out.println("result is success!");
        } else {
            System.out.println("result is fail!");
            for (String error : errorList) {
                System.out.println(error);
            }
        }
    }

    /**
     * 规则匹配
     *
     * @throws Exception
     */
    @Test
    public void matchRuleDemo() throws Exception {
        runner.addOperatorWithAlias("如果", "if", null);
        runner.addOperatorWithAlias("则", "then", null);
        runner.addOperatorWithAlias("否则", "else", null);
        DefaultContext<String, Object> context = new DefaultContext<>();
        Map<Integer, String> rules = new ImmutableMap.Builder<Integer, String>()
                .put(80, "%d>=0 && %d<100")
                .put(90, "%d>=100 && %d<200")
                .build();
        StringBuilder express = new StringBuilder();
        int value = 90;
        for (Map.Entry<Integer, String> entry : rules.entrySet()) {
            String condition = String.format(entry.getValue(), value, value);
            express.append(String.format("如果(%s)则{return %d;}", condition, entry.getKey()));
        }
        Object r = runner.execute(express.toString(), context, null, true, false);
        System.out.println("--------- r=" + r);
    }

    /**
     * 数学计算
     *
     * @throws Exception
     */
    @Test
    public void mathCalculateDemo() throws Exception {
        ExpressRunner runner = new ExpressRunner();
        runner.addMacro("总得分", "语文*语文学*权重+数数学权重+英语*英语权重");
        DefaultContext<String, Object> context = new DefaultContext<>();
        context.put("语文", 90);
        context.put("数学", 100);
        context.put("英语", 80);
        context.put("语文权重", 0.2);
        context.put("数学权重", 0.7);
        context.put("英语权重", 0.1);
        Object r1 = runner.execute("总得分", context, null, false, false);
        context.clear();
        runner.addMacro("平均分", "(语文+数学+英语)/3");
        context.put("语文", 90);
        context.put("数学", 100);
        context.put("英语", 80);
        Object r2 = runner.execute("平均分", context, null, false, false);
        System.out.println("--------- r1=" + r1);
        System.out.println("--------- r2=" + r2);
    }

    @Test
    public void mathCalculate() throws Exception {
        ExpressRunner runner = new ExpressRunner(true,false);
        runner.addMacro("calcNewQuota", "newQuota/currentMonthModulus*nextMonthModulus");
        DefaultContext<String, Object> context = new DefaultContext<>();
        context.put("newQuota", BigDecimal.valueOf(91));
        context.put("currentMonthModulus", BigDecimal.valueOf(1.09));
        context.put("nextMonthModulus", BigDecimal.valueOf(1.99));
        BigDecimal r1 = (BigDecimal) runner.execute("calcNewQuota", context, null, false, false);
//        context.put("newQuota", 91);
//        context.put("currentMonthModulus", 1.09);
//        context.put("nextMonthModulus", 1.99);
//        Double r1 = (Double) runner.execute("calcNewQuota", context, null, false, false);

        System.out.println("--------- r1=" + r1.toString());
    }
}

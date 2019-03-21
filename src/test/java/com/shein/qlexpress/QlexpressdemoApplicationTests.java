package com.shein.qlexpress;

import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressRunner;
import com.ql.util.express.IExpressContext;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class QlexpressdemoApplicationTests {

    /**
     * 精度设置
     * @throws Exception
     */
    @Test
    public void testPercise() throws Exception {
        // aIsPrecise 是否设置精度
        ExpressRunner runner = new ExpressRunner(true, false);
        IExpressContext<String,Object> expressContext = new DefaultContext<>();
        String expression ="12.3/3";
        Object result = runner.execute(expression, expressContext, null, false, false);
        System.out.println(result);
    }





    /**
     * 运算符
     * @throws Exception
     */
    @Test
    public void contextLoads() throws Exception {
        ExpressRunner runner = new ExpressRunner();
        DefaultContext<String, Object> context = new DefaultContext<>();
        context.put("a", 3*2);
        context.put("b", 2);
        String express = "(a+b)/2";
        Object r = runner.execute(express, context, null, true, true);
        Assert.assertEquals(3, r);
    }

    /**
     * 循环操作符测试
     * @throws Exception if any
     */
    @Test
    public void operateLoopTest() throws Exception {
        final String express = "int n=10;" +
                "int sum=0;" +
                "for(i=0;i<2;i++){" +
                "sum=sum+i;" +
                "}" +
                "return sum;";
        ExpressRunner runner = new ExpressRunner();
        DefaultContext<String, Object> context = new DefaultContext<>();
        Object r = runner.execute(express, context, null, true, false);
        Assert.assertEquals(1, r);
    }

    /**
     * 替换关键字 拓展操作符
     * @throws Exception
     */
    @Test
    public void replaceKeywordTest() throws Exception {
        ExpressRunner runner = new ExpressRunner();
        runner.addOperatorWithAlias("如果", "if", null);
        runner.addOperatorWithAlias("则", "then", null);
        runner.addOperatorWithAlias("否则", "else", null);
        DefaultContext<String, Object> context = new DefaultContext<>();
        final String express = "如果(1>2) 则{ return 10;} 否则 {return 5;}";
        Object r = runner.execute(express, context, null, true, false);
        Assert.assertEquals(5, r);
    }

    /**
     * 宏定义 就是简单的用一个变量替换一段文本
     * @throws Exception
     */
    @Test
    public void macroTest() throws Exception {
        ExpressRunner runner = new ExpressRunner();
        runner.addMacro("计算平均成绩", "(语文+数学+英语)/3");
        runner.addMacro("是否优秀", "计算平均成绩>90");
        IExpressContext<String, Object> context = new DefaultContext<>();
        context.put("语文", 88);
        context.put("数学", 99);
        context.put("英语", 95);
        Boolean result = (Boolean) runner.execute("是否优秀", context, null, false, false);
        Assert.assertTrue(result);
    }

    @Test
    public void bindObjectMethodTest() throws Exception {
        ExpressRunner runner = new ExpressRunner();
        DefaultContext<String, Object> context = new DefaultContext<>();

        runner.addFunctionOfClassMethod("取绝对值", Math.class.getName(), "abs",
                new String[] { "double" }, null);
        runner.addFunctionOfClassMethod("转换为大写", BindObjectMethod.class.getName(),
                "upper", new String[] { "String" }, null);
        runner.addFunctionOfServiceMethod("打印", System.out, "println",new String[] { "String" }, null);
        runner.addFunctionOfServiceMethod("contains", new BindObjectMethod(), "anyContains",
                new Class[] { String.class, String.class }, null);
        String exp = "取绝对值(-100);转换为大写(\"hello world\");打印(\"你好吗？\");contains(\"helloworld\",\"aeiou\")";
        Object r = runner.execute(exp, context, null, false, false);
        System.out.println(r);
    }

    @Test
    public void shorthandTest() throws Exception {
        ExpressRunner runner = new ExpressRunner(false,false);
        DefaultContext<String, Object> context = new DefaultContext<>();
        String express = "abc = NewMap(1:1,2:2); return abc.get(1) + abc.get(2);";
        Object r = runner.execute(express, context, null, false, false);
        Assert.assertEquals(3, r);
        express = "abc = NewList(1,2,3); return abc.get(1)+abc.get(2)";
        r = runner.execute(express, context, null, false, false);
        Assert.assertEquals(5, r);
        express = "abc = [1,2,3]; return abc[1]+abc[2];";
        r = runner.execute(express, context, null, false, false);
        Assert.assertEquals(5, r);
    }

    @Test
    public void compileScriptTest() throws Exception {
        String express = "int 平均分 = (语文+数学+英语+综合考试.科目2)/4.0;return 平均分";
        ExpressRunner runner = new ExpressRunner(true, false);
        String[] names = runner.getOutVarNames(express);
        for (String s : names) {
            System.out.println("var : " + s);
        }
    }
}

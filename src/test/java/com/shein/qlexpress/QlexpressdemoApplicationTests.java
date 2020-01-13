package com.shein.qlexpress;

import com.csvreader.CsvReader;
import com.csvreader.CsvWriter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressRunner;
import com.ql.util.express.IExpressContext;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

    @Test
    public void testCsv() throws IOException {
        // 品类 - 需求类型 map
        Map<String,String> categoryVsNeedsTypeMap = Maps.newHashMap();
        //生成CsvReader对象，以，为分隔符，GBK编码方式
        CsvReader r = new CsvReader("D://needsType.csv", ',', Charset.forName("UTF-8"));
        r.readHeaders();
        //逐条读取记录，直至读完
        while (r.readRecord()) {
            String category = r.get("category").trim();
            String needsType = r.get("needsType").trim();
            categoryVsNeedsTypeMap.put(category,needsType);
        }
        r.close();

        //

        CsvReader r1 = new CsvReader("D://id_category.csv", ',', Charset.forName("UTF-8"));
        r1.readHeaders();
        //逐条读取记录，直至读完
        while (r1.readRecord()) {
            String category = r1.get("category_name").trim();
            String id = r1.get("id").trim();
            String needsType = categoryVsNeedsTypeMap.get(category) == null ? "1" : categoryVsNeedsTypeMap.get(category);
            String sqlFormat = "update srm_business_needs_detail set needs_type = %s where id = %s;";
            String sql = String.format(sqlFormat,needsType,id);
            System.out.println(sql);

        }
        r1.close();
    }

    @Test
    public void buildSupplierPatten() throws IOException {
        //生成CsvReader对象，以，为分隔符，GBK编码方式
        CsvReader r = new CsvReader("D://1.csv", ',', Charset.forName("UTF-8"));
        r.readHeaders();
        Set<String> set1 = Sets.newHashSet();
        Set<String> set2 = Sets.newHashSet();
        //逐条读取记录，直至读完
        while (r.readRecord()) {
            String supplierId = r.get("id").trim();
            set1.add(supplierId);
        }
        r.close();
        //
        CsvReader r1 = new CsvReader("D://2.csv", ',', Charset.forName("UTF-8"));
        r1.readHeaders();
        //逐条读取记录，直至读完
        while (r1.readRecord()) {
            String id = r1.get("supplier_id").trim();
            set2.add(id);

        }
        r1.close();
        //set1 排除 set2
        System.out.println("set1 size :" + set1.size());
        System.out.println("set2 size :" + set2.size());
        set1.removeAll(set2);
        System.out.println("set1 size :" + set1.size());
        CsvWriter writer = new CsvWriter("D://3.sql");
        writer.write("sql");
        writer.endRecord();
        for (String id : set1){
            String sqlFormat = "--insert srm_supplier_patten(supplier_id,status,add_uid,add_time) VALUES(%s,2,'System',NOW());--";
            String sql = String.format(sqlFormat,id);
            writer.write(sql);
            writer.endRecord();
        }
        writer.close();

    }

    @Test
    public void build() throws IOException {
        CsvReader r = new CsvReader("D://3.sql", ',', Charset.forName("UTF-8"));
        r.readHeaders();
        CsvWriter writer = new CsvWriter("D://4.sql");
        while (r.readRecord()) {
            String sql = r.get("sql").trim();
            sql = sql.replaceAll("\"","--");
            writer.write(sql);
            writer.endRecord();
        }
        r.close();
        writer.close();


    }
}

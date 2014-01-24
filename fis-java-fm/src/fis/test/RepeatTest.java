package fis.test;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import cn.tianya.fw.front.FisException;
import cn.tianya.fw.front.FisResource;
import cn.tianya.fw.front.util.FtlUtil;
import freemarker.template.Template;
import freemarker.template.TemplateException;
  
/**  
 *   
 * 客户端测试模板输入类  
 */  
public class RepeatTest {   
    public static void main(String[] args) {   
    	long startTime = new Date().getTime();
    	for(int loop = 10; loop > 0;loop--){
	        Map<String,Object> root=new HashMap<String, Object>();   
	        root.put("repeat", new RepeatDirective());
	        try {
	        	FisResource fisRes = new FisResource();
	        	Template template = FtlUtil.getTemplate(RepeatTest.class
						.getClassLoader().getResource("./fis/test/templates").getPath(), "repeat.ftl", "UTF-8");
	        	FtlUtil.processTemplate(template, root, new OutputStreamWriter(System.out), fisRes);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (TemplateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (FisException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}   
    	}
    	long endTime = new Date().getTime();
    	System.out.println("共花费时间：" + (endTime - startTime));
        
    }   
}  

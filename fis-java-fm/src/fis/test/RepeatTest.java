package fis.test;

import java.io.IOException;
import java.io.OutputStreamWriter;   
import java.util.Date;
import java.util.HashMap;   
import java.util.Map;   


import fis.front.FISException;
import fis.front.FISResource;
import fis.front.FreeMarkertUtil;
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
	        	FISResource fisRes = new FISResource("./fis/test/config");
	        	Template template = FreeMarkertUtil.getTemplate(RepeatTest.class
						.getClassLoader().getResource("./fis/test/templates").getPath(), "repeat.ftl", "UTF-8");
	        	FreeMarkertUtil.processTemplate(template, root, new OutputStreamWriter(System.out), fisRes);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (TemplateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (FISException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}   
    	}
    	long endTime = new Date().getTime();
    	System.out.println("共花费时间：" + (endTime - startTime));
        
    }   
}  

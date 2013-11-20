package fis.test.demo;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import fis.front.FISResource;
import fis.front.FisDataUtil;
import fis.front.FreeMarkertUtil;
import freemarker.ext.servlet.FreemarkerServlet;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;

public class FISFreemarkerServlet extends HttpServlet {
	private Configuration cfg;
	
	public FISFreemarkerServlet() {
        super();
    }
	public void init() {
		// 初始化FreeMarker配置
		// 创建一个Configuration实例
		cfg = new Configuration();
		// 设置FreeMarker的模版文件位置
		cfg.setServletContextForTemplateLoading(getServletContext(),
				"template");
		//设置包装器，并将对象包装为数据模型   
		cfg.setObjectWrapper(new DefaultObjectWrapper());   
		

	}

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {
			long startTime = new Date().getTime();
			//System.out.println("startTime : " + startTime);
				
			// 建立数据模型
			Map<String, Object> root = new HashMap<String, Object>();
			// 放入对应数据key value
			Map<String, Object> data = FisDataUtil.getJSON("./data/fis_data.json");
			Map<String, Object> data2 = FisDataUtil.getJSON("./data/test_data.json");
			
			root.putAll(data);
			root.putAll(data2);
			// 取得模版文件
			Template t = cfg.getTemplate("./page/index.ftl", "utf-8");
	
			// 开始准备生成输出
			// 使用模版文件的charset作为本页面的charset
			// 使用text/html MIME-type
			response.setContentType("text/html; charset=" + t.getEncoding());
			PrintWriter out = response.getWriter();
			
			FISResource fisRes = new FISResource();
			// 合并数据模型和模版，并将结果输出到out中
			FreeMarkertUtil.processTemplate(t, root, out, fisRes);

			long endTime = new Date().getTime();
	    	System.out.println("共花费时间：" + (endTime - startTime));
		}  catch (TemplateException e) {
			e.printStackTrace();
		}  catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
}


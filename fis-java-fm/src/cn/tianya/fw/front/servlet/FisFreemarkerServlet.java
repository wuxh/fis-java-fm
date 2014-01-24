package cn.tianya.fw.front.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.tianya.fw.front.FisException;
import cn.tianya.fw.front.FisResource;
import cn.tianya.fw.front.rewrite.FisRewrite;
import cn.tianya.fw.front.rewrite.FisRewriteRule;
import cn.tianya.fw.front.util.FisModelSimulator;
import cn.tianya.fw.front.util.FtlUtil;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;

public class FisFreemarkerServlet extends HttpServlet {
	private Configuration cfg;
	private FisRewrite fr;
	
	public FisFreemarkerServlet() {
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
		try {
			fr = FisRewrite.getInstance();
		} catch (FisException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

	}

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String requestUri = request.getRequestURI();
		FisRewriteRule frRule = fr.findRule(requestUri);
		try {
			// 取得模版文件
			Template t = cfg.getTemplate(frRule.getTemplateFile(), "utf-8");
				
			// 建立数据模型
			Map<String, Object> root = new HashMap<String, Object>();
			List<String> dataFiles = frRule.getDataFiles();
			for(int i = 0, len = dataFiles.size(); i < len; i++){
				// 放入对应数据key value
				Map<String, Object> data = FisModelSimulator.getJSON(dataFiles.get(i));
				root.putAll(data);
			}

			// 开始准备生成输出
			// 使用模版文件的charset作为本页面的charset
			// 使用text/html MIME-type
			response.setContentType("text/html; charset=" + t.getEncoding());
			PrintWriter out = response.getWriter();
			
			FisResource fisRes = new FisResource();
			// 合并数据模型和模版，并将结果输出到out中
			FtlUtil.processTemplate(t, root, out, fisRes);


		}  catch (TemplateException e) {
			e.printStackTrace();
		}  catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
}


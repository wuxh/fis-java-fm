package fis.front;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import freemarker.core.Environment;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.ObjectWrapper;
import freemarker.template.SimpleHash;
import freemarker.template.SimpleNumber;
import freemarker.template.Template;
import freemarker.template.TemplateBooleanModel;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateHashModel;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateNumberModel;
import freemarker.template.TemplateScalarModel;

public class WidgetDirective implements TemplateDirectiveModel{
	
	private static final String PARAM_NAME_NAME = "name";   
    private static final String PARAM_NAME_CALL = "call";   

    private FISResource fisRes;

	public WidgetDirective(FISResource fisRes) {
		super();
		this.fisRes = fisRes;
	}


	public void execute(Environment env, Map params, TemplateModel[] loopVars,   
            TemplateDirectiveBody body) throws TemplateException, IOException {
		// ---------------------------------------------------------------------   
        // 处理参数   
    
        String name = "";
        String call = "";
  
        Iterator paramIter = params.entrySet().iterator();   
        while (paramIter.hasNext()) {   
            Map.Entry ent = (Map.Entry) paramIter.next();   
  
            String paramName = (String) ent.getKey();   
            TemplateModel paramValue = (TemplateModel) ent.getValue();   
  
            if (paramName.equals(PARAM_NAME_NAME)) {   
                if (!(paramValue instanceof TemplateScalarModel)) {   
                    throw new TemplateModelException("The \"" + PARAM_NAME_NAME   
                            + "\" parameter " + "must be a string.");   
                }   
                name = ((TemplateScalarModel) paramValue).getAsString(); 
            } else if(paramName.equals(PARAM_NAME_CALL)) {   
                if (!(paramValue instanceof TemplateScalarModel)) {   
                    throw new TemplateModelException("The \"" + PARAM_NAME_CALL   
                            + "\" parameter " + "must be a string.");   
                }   
                call = ((TemplateScalarModel) paramValue).getAsString(); 
            } 
        }   


		String ftl_uri;
		try {
			ftl_uri = fisRes.load(name);
		} catch (FISException e) {
			throw new TemplateException(e.getMessage(), e, env);
		}

		// 获取模板,并设置编码方式，这个编码必须要与页面中的编码格式一致
		Template template = env.getConfiguration().getTemplate(ftl_uri, "UTF-8");
		env.include(template);

        
	}

}

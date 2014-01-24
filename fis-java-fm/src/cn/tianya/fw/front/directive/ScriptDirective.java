package cn.tianya.fw.front.directive;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;

import cn.tianya.fw.front.FisResource;
import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

public class ScriptDirective implements TemplateDirectiveModel {

	private FisResource fisRes;

	public ScriptDirective(FisResource fisRes) {
		super();
		this.fisRes = fisRes;
	}

	public void execute(Environment env, Map params, TemplateModel[] loopVars,   
            TemplateDirectiveBody body) throws TemplateException, IOException {
        if (body != null) {     
            StringWriter sOut = new StringWriter();    
            body.render(sOut);   
            
            String script = sOut.getBuffer().toString();
            fisRes.addScript(script);
        } 
		
	}
	

}

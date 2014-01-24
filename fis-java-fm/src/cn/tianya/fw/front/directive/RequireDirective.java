package cn.tianya.fw.front.directive;

import java.io.IOException;
import java.util.Map;

import cn.tianya.fw.front.FisException;
import cn.tianya.fw.front.FisResource;

import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateScalarModel;

public class RequireDirective implements TemplateDirectiveModel {

	private FisResource fisRes;

	public RequireDirective(FisResource fisRes) {
		super();
		this.fisRes = fisRes;
	}

	public void execute(Environment env, Map params, TemplateModel[] loopVars,   
            TemplateDirectiveBody body) throws TemplateException, IOException {
  
        String name = null;
		TemplateModel paramValue = (TemplateModel)params.get("name");
		if(paramValue != null){
			name = ((TemplateScalarModel) paramValue).getAsString();
		}
		if(name == null || "".equals(name)){
			return;
		}
    	try {
			String url = fisRes.require(name);
		} catch (FisException e) {
			throw new TemplateException(e.getMessage(), e, env);
		}



	}


}

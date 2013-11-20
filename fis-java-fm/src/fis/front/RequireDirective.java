package fis.front;

import java.io.IOException;
import java.util.Map;

import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

public class RequireDirective implements TemplateDirectiveModel {

	private FISResource fisRes;

	public RequireDirective(FISResource fisRes) {
		super();
		this.fisRes = fisRes;
	}

	public void execute(Environment env, Map params, TemplateModel[] loopVars,   
            TemplateDirectiveBody body) throws TemplateException, IOException {
  
        String name = params.get("name").toString();
    	try {
			String url = fisRes.load(name);
		} catch (FISException e) {
			throw new TemplateException(e.getMessage(), e, env);
		}



	}


}

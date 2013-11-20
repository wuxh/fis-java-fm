package fis.front;

import java.io.*;
import java.util.*;


import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateScalarModel;

public class HtmlDirective implements TemplateDirectiveModel {
	
	private FISResource fisRes;

	public HtmlDirective(FISResource fisRes) {
		super();
		this.fisRes = fisRes;
	}

	public void execute(Environment env, Map params, TemplateModel[] loopVars,   
            TemplateDirectiveBody body) throws TemplateException, IOException {
		String tagBeginHtml = tagBegin(params);

        Writer out = env.getOut();   
        if (body != null) {    
            out.write(tagBeginHtml);   
            
            StringWriter sOut = new StringWriter();   
            body.render(sOut);   
            
            out.write(this.fisRes.renderResponse(sOut.getBuffer().toString()));
            out.write("\n</html>"); 
        } 
		
	}
	
	private String tagBegin(Map params){
		String attrs = "";
		Set<String> keys =  params.keySet();
		for(Iterator<String> it = keys.iterator(); it.hasNext();){
			String key = it.next();
			attrs += " " + key + "=\"" + params.get(key) + "\";";
		}
		return "<!doctype html>\n<html " + attrs + ">\n";
	}
	

}

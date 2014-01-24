package cn.tianya.fw.front.directive;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import cn.tianya.fw.front.FisException;
import cn.tianya.fw.front.FisResource;
import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

public class BodyDirective implements TemplateDirectiveModel {
	private FisResource fisRes;

	public BodyDirective(FisResource fisRes) {
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
            
            out.write(sOut.getBuffer().toString());
            
            out.write("\n</body>\n"); 
            try {
				out.write(fisRes.getRenderFrag("js"));
			} catch (FisException e) {
				throw new TemplateException(e.getMessage(), e, env);
			} 
            out.write(fisRes.getRenderScript()); 
        } 
		
	}
	
	private String tagBegin(Map params){
		String attrs = "";
		Set<String> keys =  params.keySet();
		for(Iterator<String> it = keys.iterator(); it.hasNext();){
			String key = it.next();
			attrs += " " + key + "=\"" + params.get(key) + "\";";
		}
		return "<body " + attrs + ">\n";
	}
}

package cn.tianya.fw.front.jsp;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.tagext.BodyTagSupport;

import cn.tianya.fw.front.FisResource;

public class ScriptTag extends BodyTagSupport {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2298651716053938808L;
	
	public int doStartTag(){
		return EVAL_BODY_BUFFERED;
	}

	public int doEndTag(){
		HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
		FisResource resource = (FisResource) request.getAttribute(FisResource.CONTEXT_ATTR_NAME);
		BodyContent body = this.getBodyContent();
		String code = body.getString();
		resource.addScript(code);
		return EVAL_PAGE;
	}
}

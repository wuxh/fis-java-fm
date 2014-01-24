package cn.tianya.fw.front.jsp;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.tagext.BodyTagSupport;

import cn.tianya.fw.front.FisException;
import cn.tianya.fw.front.FisResource;

public class HtmlTag extends BodyTagSupport {
	
	private FisResource resource;

	/**
	 * 
	 */
	private static final long serialVersionUID = -612383582047754887L;
	
	public int doStartTag() throws JspException{
		JspWriter out = pageContext.getOut();
		try {
			out.append("<html>");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
		try {
			resource = new FisResource();
		} catch (FisException e) {
			throw new JspException(e);
		}
		request.setAttribute(FisResource.CONTEXT_ATTR_NAME, resource);
		return EVAL_BODY_BUFFERED;
	}
	
	public int doEndTag(){
		BodyContent body = this.getBodyContent();
		String html = body.getString() + "</html>";
		try {
			html = resource.replace(html);
		} catch (FisException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		JspWriter out = pageContext.getOut();
		try {
			out.write(html);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return EVAL_PAGE;
	}
}
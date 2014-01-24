package cn.tianya.fw.front.jsp;

import java.io.IOException;

import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import cn.tianya.fw.front.FisResource;

public class ScriptsTag extends TagSupport {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8981028602817984686L;
	
	public int doStartTag(){
		JspWriter out = pageContext.getOut();
		try {
			out.append(FisResource.SCRIPT_PLACEHOLDER);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return SKIP_BODY;
	}
}

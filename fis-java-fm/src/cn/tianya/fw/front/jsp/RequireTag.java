package cn.tianya.fw.front.jsp;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.tagext.TagSupport;

import cn.tianya.fw.front.FisResource;

public class RequireTag extends TagSupport {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8694856279353734532L;
	
	private String id;
	
	public int doStartTag() {
		HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
		FisResource resource = (FisResource) request.getAttribute(FisResource.CONTEXT_ATTR_NAME);
		try {
			resource.require(this.id);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return SKIP_BODY;
	}

	public void setId(String id) {
		this.id = id;
	}

}

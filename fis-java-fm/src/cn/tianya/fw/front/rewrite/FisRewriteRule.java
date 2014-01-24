package cn.tianya.fw.front.rewrite;

import java.util.List;

public class FisRewriteRule {
	private String requestUri;
	private String templateFile;
	private List<String> dataFiles;
	
	public FisRewriteRule(String requestUri, String templateFile, List<String> dataFiles){
		this.requestUri = requestUri;
		this.templateFile = templateFile;
		this.dataFiles = dataFiles;
	}

	public String getRequestUri() {
		return requestUri;
	}

	public String getTemplateFile() {
		return templateFile;
	}

	public List<String> getDataFiles() {
		return dataFiles;
	}

}

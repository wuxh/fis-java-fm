package cn.tianya.fw.front.rewrite;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.tianya.fw.front.FisException;
import cn.tianya.fw.front.util.FisModelSimulator;



public class FisRewrite {
	
	/**
     * 调度间隔,单位秒
     */
    private static final long INTERVAL = 5;
	
	private static final String DEFAULT_SERVER_CONFIG_DIR = "./config/";
	private static final String DEFAULT_SERVER_CONFIG_FILE = "server.json";
	
	private static List<FisRewriteRule> frRules = new ArrayList<FisRewriteRule>();;
	private static FisRewrite instance = null;
	
	/**
     * 日志
     */
    private static final Logger logger = LoggerFactory.getLogger(FisRewrite.class);

	private FisRewrite() throws FisException{
		this.initAll();
	}
	
	public static FisRewrite getInstance() throws FisException{
		if(instance == null){
			instance = new FisRewrite();
		}
		return instance;
	}
	
	public static void init(String fileName) throws FisException{
		
		
		Map<String, Object> rulesMap = FisModelSimulator.getJSON(DEFAULT_SERVER_CONFIG_DIR + fileName);
		List<Map<String, Object>> rules = (List<Map<String, Object>>)(rulesMap.get("rules"));
		if(null == rules){
			throw new FisException("配置文件server.json的rules属性不能为空");
		}
		for(int i=0, len = rules.size(); i < len; i++){
			Map<String, Object> rule = rules.get(i);
			String requestUri = (String)rule.get("requestUri");
			String templateFile = (String)rule.get("templateFile");
			List<String> dataFiles = (List<String>)rule.get("dataFiles");
			if(StringUtils.isBlank(requestUri)){
				throw new FisException("配置文件server.json的第" + (i+1)+ "条规则配置对象中的requestUri属性不能为空");
			}
			if(StringUtils.isBlank(templateFile)){
				throw new FisException("配置文件server.json的第" + (i+1)+ "条规则配置对象中的templateFile属性不能为空");
			}
			if(dataFiles == null){
				dataFiles = new ArrayList<String>();
			}
			
			FisRewriteRule frRule = new FisRewriteRule(requestUri, templateFile, dataFiles);
			frRules.add(frRule);
		}
	}
	
	private static void initAll() throws FisException {
        String configDirPath = FisRewrite.class.getClassLoader().getResource(DEFAULT_SERVER_CONFIG_DIR).getFile();

        File serverJsonDir = new File(configDirPath);
        String[] serverJsonFiles = serverJsonDir.list();

        FilenameFilter defaultFileNameFilter = new FilenameFilter() {
            public boolean accept(File dir, String name) {
                boolean isMatch = name.equals(DEFAULT_SERVER_CONFIG_FILE);
                return isMatch;

            }
        };
        FilenameFilter nameSpaceFileNameFilter = new FilenameFilter() {
            public boolean accept(File dir, String name) {
                boolean isMatch = name.endsWith("-" + DEFAULT_SERVER_CONFIG_FILE);
                return isMatch;

            }
        };

        String[] childrenFileNames = serverJsonDir.list(defaultFileNameFilter);
        String[] nameSpacechildrenFileNames = serverJsonDir.list(nameSpaceFileNameFilter);

        if ((null == childrenFileNames || 0 == childrenFileNames.length)
                && (null == nameSpacechildrenFileNames || 0 == nameSpacechildrenFileNames.length)) {
            throw new FisException("server config File server.json Not Found!");
        }

        // 注册缺省的映射文件
        if (null != childrenFileNames || 0 < childrenFileNames.length) {
            for (int i = 0; i < childrenFileNames.length; i++) {
                FisRewrite.init(childrenFileNames[i]);
            }
        }

        // 注册包含命名空间的
        if (null != nameSpacechildrenFileNames || 0 < nameSpacechildrenFileNames.length) {
            for (int i = 0; i < nameSpacechildrenFileNames.length; i++) {
                FisRewrite.init(nameSpacechildrenFileNames[i]);
            }
        }

        // for (int i = 0, len = mapJsonFiles.length; i < len; i++) {
        // String fileName = mapJsonFiles[i];
        // if (fileName.equals(FisResource.DEFAULT_RES_MAPPING_FILE)) {
        // FisResource.register(FisResource.DEFAULT_MAPPING_FILE_DIR,
        // FisResource.DEFAULT_NS_GLOBAL);
        // } else {
        // int pos = fileName.indexOf("-" +
        // FisResource.DEFAULT_RES_MAPPING_FILE);
        // if (pos > 0) {
        // String namespace = fileName.substring(0, pos);
        // FisResource.register(FisResource.DEFAULT_MAPPING_FILE_DIR,
        // namespace);
        // }
        // }
        // }
    }

	public void setRules(List<FisRewriteRule> rules) {
		this.frRules = rules;
	}

	public List<FisRewriteRule> getRules() {
		return frRules;
	}
	
	public FisRewriteRule findRule(String requestUri){
		for(int i = 0, len = this.frRules.size(); i < len; i++){
			FisRewriteRule rule = this.frRules.get(i);
			Pattern pattern = Pattern.compile(rule.getRequestUri());
			Matcher mat = pattern.matcher(requestUri);  
			if(mat.find()){
				return rule;
			}
		}
		return null;
	}
	
	public static void main(String[] args){
		try {
			FisRewrite fr = new FisRewrite();
			List<FisRewriteRule> rules = fr.getRules();
			for(int i=0; i < rules.size(); i++){
				FisRewriteRule rule = rules.get(i);
				System.out.println(rule.getRequestUri());
				System.out.println(rule.getTemplateFile());
				List<String> dataFiles = rule.getDataFiles();
				if(null != dataFiles){
					for(int j = 0, len = dataFiles.size(); i < len; i++){
						System.out.println(dataFiles.get(i));
					}
				}
				
			}
			String requestUri = "/index.shtml";
			FisRewriteRule frRule = fr.findRule(requestUri);
			System.out.println("find : " + frRule.getRequestUri());
		} catch (FisException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}

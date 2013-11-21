package fis.front;

import java.io.*;
import java.util.*;

import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;




public class FISResource {
	private   static final String CSS_LINKS_HOOK = "<!--[FIS_CSS_LINKS_HOOK]-->";
	private   static final String DEFAULT_CONFIG_DIR = "./config/";
	private   static final String DEFAULT_MAP_JSON_NAME = "map.json";
	public static final String DEFAULT_NS_GLOBAL = "__global__";
	
	private static Map<String,Map> resMap = new HashMap<String,Map>();
    private Map<String,String> resLoaded;
    private Map<String,List<String>> staticCollection;
    private LinkedHashSet<String> scriptPool;
    private String configDir;
    private static Map<String, Long> lastModifiedTimes = new HashMap<String, Long>();
    
    /**
	 * 日志
	 */
	private static final Logger logger = Logger
			.getLogger(FISResource.class);
    
	
	/**
	 * 初始化操作
	 */
	static {
		 


		
		Timer timer = new Timer(true); 
		timer.schedule(
			new TimerTask() { 
				public void run() { 
					try {
						registerAll(FISResource.DEFAULT_CONFIG_DIR);
					} catch (FISException e) {
						logger.error("定时加载mapJson文件出错！", e);
					}
				} 
			},0, 1*60*1000); 
	}
    
	public FISResource() throws FISException {
    	this.configDir = FISResource.DEFAULT_CONFIG_DIR;
    	resLoaded = new HashMap<String,String>();
        staticCollection = new HashMap<String,List<String>>();
        scriptPool = new LinkedHashSet<String>();       
    }
	
	/**
	 * 构造函数
	 * @param configDir 存放map.json文件的配置目录
	 * @throws FISException
	 */
    public FISResource(String configDir) throws FISException {
    	this.configDir = configDir;
    	resLoaded = new HashMap<String,String>();
        staticCollection = new HashMap<String,List<String>>();
        scriptPool = new LinkedHashSet<String>();       
    }
    
    
    private void reset(){
    	resLoaded = null;
        staticCollection = null;
        scriptPool = null;
    }
    
    /**
     * 返回css占位符字符串
     * @return
     */
    public static String cssHook(){
        return CSS_LINKS_HOOK;
    }
    
    /**
     * 将内容中的css占位符替换为最终的css内容
     * @param content
     * @return
     */
    public String renderResponse(String content){
        if(content.indexOf(CSS_LINKS_HOOK) >= 0){
        	content = content.replace(CSS_LINKS_HOOK, render("css"));
        }
        reset();
        return content;
    }
    
    /**
     * 将指定类型的资源列表转换为html代码输出
     * @param type
     * @return
     */
    public String render(String type){
        String html = "";
        if(staticCollection.containsKey(type)){
        	List<String> uris = staticCollection.get(type);
        	if("js" == type){
        		for(int i=0, len=uris.size(); i < len; i++){
        			html += "<script type=\"text/javascript\" src=\"" + uris.get(i) + "\"></script>";
        		}
        	} else if("css" == type){
        		for(int i=0, len=uris.size(); i < len; i++){
        			html += "<link rel=\"stylesheet\" type=\"text/css\" href=\"" + uris.get(i) + "\"/>";
        		}
        	}
        }
        
        return html;
    }
    
    /**
     * 添加script代码片段到script资源列表中
     * @param script
     */
    public void addScriptPool(String script){
    	scriptPool.add(script);
    }
    
    /**
     * 将收集到的script片段资源列表转换为html代码
     * @return
     */
    public String renderScriptPool(){
        String html = "";
        if(!scriptPool.isEmpty()){
        	for(Iterator<String> it = scriptPool.iterator(); it.hasNext();){
        		String script = it.next();
    			html += "<script type=\"text/javascript\">!function(){" + script + " }();</script>";
    		}
            
        }
        return html;
    }
    
    /**
     * 注册命名空间，并加载对应的map.json文件
     * @param namespace
     * @return
     * @throws FISException 
     */
    private boolean register(String namespace) throws FISException{
    	return register(this.configDir, namespace);
    }
    
    /**
     * 注册命名空间，并加载对应的map.json文件
     * @param namespace
     * @return
     * @throws FISException 
     */
    public static boolean register(String configDir, String namespace) throws FISException{
    	String mapName = "map.json";
        if(namespace != FISResource.DEFAULT_NS_GLOBAL){
            mapName = namespace + "-map.json";
        }
        
        String mapPath = configDir + "/" + mapName;
        
        
        // 加载配置数据
		BufferedReader br = null;
		try{
			String fileName = FISResource.class
			.getClassLoader().getResource(mapPath).getPath();
			File mapJsonFile = new File(fileName);
			if (null == lastModifiedTimes.get(fileName) || lastModifiedTimes.get(fileName) != mapJsonFile.lastModified()) {
				
				ObjectMapper mapper = new ObjectMapper(); // can reuse, share globally
				resMap.put(namespace, mapper.readValue(mapJsonFile, HashMap.class));
				
				// 重置lastModifiedTime
				lastModifiedTimes.put(fileName, mapJsonFile.lastModified());
			}
			return true;
		} catch (Exception e) {
			throw new FISException("加载mapJson文件数据出错！",e);
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
				}
			}
		}
        
        
    }
    
    private static void registerAll(String configDir) throws FISException {
		String configDirPath = FISResource.class
		.getClassLoader().getResource(configDir).getFile();

		File mapJsonDir = new File(configDirPath);
		String[] mapJsonFiles = mapJsonDir.list();
		for(int i = 0, len = mapJsonFiles.length; i < len; i++ ){
			String fileName = mapJsonFiles[i];
			if(fileName.equals(FISResource.DEFAULT_MAP_JSON_NAME)){
				FISResource.register(FISResource.DEFAULT_CONFIG_DIR, FISResource.DEFAULT_NS_GLOBAL);
			} else {
				int pos = fileName.indexOf("-" + FISResource.DEFAULT_MAP_JSON_NAME);
				if(pos>0){
					String namespace = fileName.substring(0, pos);
					FISResource.register(FISResource.DEFAULT_CONFIG_DIR, namespace);
				}
			}
		}
	}
    
    /**
     * 从map.json资源文件中获取指定name对应的资源路径
     * @param name
     * @return
     * @throws FISException 
     */
    public String load(String name) throws FISException{
        String loadedURI = resLoaded.get(name);
        if(null != loadedURI && "" != loadedURI){
        	return loadedURI;
        } else {
        	String uri = "";
        	int pos = name.indexOf(":");
        	String namespace = FISResource.DEFAULT_NS_GLOBAL;
        	if(name.indexOf(":") >= 0) {
        		namespace = name.substring(0, pos);
        	}

        	if(resMap.containsKey(namespace) || register(namespace)){
        		Map<String, Map> nsResMap = resMap.get(namespace);
        		Map res = (Map) nsResMap.get("res").get(name);
        		if(res != null && !res.isEmpty()){
        			if(res.containsKey("pkg")){
        				Map pkg = (Map) nsResMap.get("pkg").get(res.get("pkg"));
        				uri = (String) pkg.get("uri");
        				List<String> has = (List<String>) pkg.get("has"); 
        				for(int i = 0, len = has.size(); i < len; i++){
        					resLoaded.put(has.get(i), uri);
        				}
        			} else {
        				if(res.containsKey("deps")){
        					List<String> deps = (List<String>)res.get("deps");
        					for(int i = 0, len = deps.size(); i < len; i++){
        						load(deps.get(i));
        					}
        				}
        				uri = (String) res.get("uri");
        				resLoaded.put(name, uri);
        			}
        			if(null == staticCollection.get(res.get("type"))){
        				staticCollection.put((String) res.get("type"), new ArrayList<String>());
        			}
        			staticCollection.get(res.get("type")).add(uri);
        			loadedURI = uri;
        		}
        	} else {
        		throw new FISException("missing map file of \"" +  namespace);
        	}
        }
		return loadedURI;
        
    }

	
    
}

package cn.tianya.fw.front;

import java.io.*;
import java.util.*;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * 前端资源映射类
 * @see <a href="http://wiki.yanfa.tianya.cn/wiki/doku.php?id=front-end:fis">fis相关文档wiki</a>
 * @author wuxh  <wuxh@staff.tianya.cn>
 * @date   Nov 29, 2013 9:44:53 AM
 */
public class FisResource {
    /**
     * 调度间隔,单位秒
     */
    private static final long INTERVAL = 60;

    public static final String CONTEXT_ATTR_NAME = "com.baidu.fis.resource";
    public static final String STYLE_PLACEHOLDER = "<!--FIS_STYLE_PLACEHOLDER-->";
    public static final String SCRIPT_PLACEHOLDER = "<!--FIS_SCRIPT_PLACEHOLDER-->";
    private static final String DEFAULT_MAPPING_FILE_DIR = "./config/";
    private static final String DEFAULT_RES_MAPPING_FILE = "map.json";
    public static final String DEFAULT_NS_GLOBAL = "__global__";

    private static Map<String, Map> resMap = new HashMap<String, Map>();
    private Map<String, String> resLoadedMap;
    private Map<String, List<String>> staticResourceMap;
    private Map<String,Map<String,Object>> requireAsyncCollection;
    private LinkedHashSet<String> scriptPoolSet;
    private String framework;
    private static Map<String, Long> lastModifiedTimes = new HashMap<String, Long>();

    /**
     * 日志
     */
    private static final Logger logger = LoggerFactory.getLogger(FisResource.class);


    /**
     * 初始化操作
     */
    static {
    	
    	

        // 定时服务

		Timer timer = new Timer("Front Resource Mapping map.json的WatchDog线程", true);
        timer.schedule(new TimerTask() {
            public void run() {
                try {
                    //logger.info("Loading Front Resource Mapping File!");
                    registerAll(FisResource.DEFAULT_MAPPING_FILE_DIR);
                } catch (FisException e) {
                    logger.error("Loading Resource Mapping File error", e);
                }
            }
        }, 0, INTERVAL);

		
    }

    public FisResource() throws FisException {
        resLoadedMap = new HashMap<String, String>();
        staticResourceMap = new HashMap<String, List<String>>();
        requireAsyncCollection = new HashMap<String,Map<String,Object>>(); 
        scriptPoolSet = new LinkedHashSet<String>();
    }
    
    
    /**
     * 获取静态资源URI,有包的时候，返回包的uri,从map.json资源文件中
     * 
     * @param key
     *            - 资源key,例如static/css/blog.css
     * @return String
     * @throws FisException
     */
    public static String getResourceUri(String name) throws FisException{
    	Map<String, Map> nsResMap = getNsResMap(name);
    	Map<String, Object> res = getRes(nsResMap, name);  	
    	return getResourceUri(name, nsResMap, res);
    }

    

    /**
     * 返回css占位符字符串
     * 
     * @return
     */
    public static String getCssHook() {
        return STYLE_PLACEHOLDER;
    }
    
    /**
     * 设置js Framework
     * @param value
     */
    public void setFramework(String value){
    	this.framework = value;
    }

    /**
     * 将内容中的css占位符替换为最终的css内容
     * 
     * @param content
     * @return
     * @throws FisException 
     */
    public String renderResponse(String content) throws FisException {
        if (content.indexOf(STYLE_PLACEHOLDER) >= 0) {
            content = content.replace(STYLE_PLACEHOLDER, this.getRenderFrag("css"));
        }
        reset();
        return content;
    }
    
    public String replace(String html) throws FisException{
        html = html.replace(FisResource.STYLE_PLACEHOLDER, this.getRenderFrag("css"));
        html = html.replace(FisResource.SCRIPT_PLACEHOLDER, this.getRenderFrag("js") + this.getRenderScript());
        return html;
    }

    /**
     * 将指定类型的资源列表转换为html代码输出
     * 
     * @param type
     *             资源类型,例如js,css
     * @return
     * @throws FisException 
     */
    public String getRenderFrag(String type) throws FisException {
        String html = "";
        if (staticResourceMap.containsKey(type)) {
            List<String> uris = staticResourceMap.get(type);
            if ("js" == type) {
            	String resourceMap = this.getResourceMap();
            	if(StringUtils.isNotBlank(this.framework)){ 
            		if(this.staticResourceMap.containsKey("js") || !(null == resourceMap || "".equals(resourceMap))){
            			html += "<script type=\"text/javascript\" src=\"" + this.framework + "\"></script>";
            		}
            	}

            	if(!resourceMap.isEmpty()){
            		html += "<script type=\"text/javascript\">";
            		html += "require.resourceMap("+ resourceMap.toString() + ")";
            		html += "</script>";
            	}
                for (int i = 0, len = uris.size(); i < len; i++) {
                	if(uris.get(i).equals(this.framework)){
        				continue;
        			}
                    html += "<script type=\"text/javascript\" src=\"" + uris.get(i) + "\"></script>";
                }
            } else if ("css" == type) {
                for (int i = 0, len = uris.size(); i < len; i++) {
                    html += "<link rel=\"stylesheet\" type=\"text/css\" href=\"" + uris.get(i) + "\"/>";
                }
            }
        }

        return html;
    }
    
    /**
     * 将收集到的script片段资源列表转换为html代码
     * 
     * @return String 返回html的script代码片段
     */
    public String getRenderScript() {
        String html = "";
        if (!scriptPoolSet.isEmpty()) {
            for (Iterator<String> it = scriptPoolSet.iterator(); it.hasNext();) {
            	String script = it.next();
    			html += "<script type=\"text/javascript\">";
    			html += "\n!function(){" + script + " }();";
    			html += "</script>";
            }

        }
        return html;
    }
    

    /**
     * 获取异步js资源集合，变为json格式的字符串
     * @return
     * @throws FisException
     */
    private String getResourceMap() throws FisException {
		String ret = "";
		Map<String, Map<String, Object>> resourceMap = new HashMap<String, Map<String, Object>>();
		if(this.requireAsyncCollection.containsKey("res")){
			for(Iterator<String> it = this.requireAsyncCollection.get("res").keySet().iterator(); it.hasNext();){
				String id = it.next();
				Map<String, Object> res = (Map<String, Object>)this.requireAsyncCollection.get("res").get(id);
				List<String> jsDeps = new ArrayList<String>();
				if(res.containsKey("deps")){
					List<String> deps = (List<String>)res.get("deps");
					for(int i=0,len=deps.size(); i < len; i++){
						String name = deps.get(i);
						if(name.endsWith(".js")){
							jsDeps.add(name);
						}
					}
				}
				
				Map<String, Object> item = new HashMap<String, Object>();
				item.put("url", res.get("uri"));
				if(res.containsKey("pkg")){
					item.put("pkg", res.get("pkg"));
				}
				if(!jsDeps.isEmpty()){
					item.put("deps", jsDeps);
				}
				if(resourceMap.get("res") == null){
					resourceMap.put("res", new HashMap<String, Object>());
				}
				resourceMap.get("res").put(id, item);
				
			}
		}
		
		if(this.requireAsyncCollection.containsKey("pkg")){
			Map<String, Object> pkg = this.requireAsyncCollection.get("pkg");
			for(Iterator<String> it = pkg.keySet().iterator();it.hasNext();){
				String id = it.next();
				Map<String, Object> res = this.requireAsyncCollection.get(id);
				resourceMap.get(pkg).put(id, new HashMap<String, Object>().put("url", res.get("uri")));
			}
		}
		if(!resourceMap.isEmpty()){
			ObjectMapper mapper = new ObjectMapper(); // can reuse, share globally
			try {
				ret = mapper.writeValueAsString(resourceMap);
			} catch (Exception e) {
				throw new FisException("resourceMap 转换为json字符串出错。 ",e);
			} 
			 
		}
		return ret;
	}

    /**
     * 添加script代码片段到script资源列表中
     * 
     * @param script
     */
    public void addScript(String script) {
        scriptPoolSet.add(script);
    }
    
    /**
     * 获取静态资源URI,从map.json资源文件中，默认同步加载的方式
     * @param name
     * @return
     * @throws FisException
     */
    public String load(String name) throws FisException{
    	return this.load(name, false);
    }
    
    public String require(String name) throws FisException{
    	return this.load(name);
    }

    
    
    private void reset() {
        resLoadedMap = null;
        staticResourceMap = null;
        scriptPoolSet = null;
        requireAsyncCollection = null;
        framework = null;
    }

    /**
     * 注册命名空间，并加载对应的map.json文件
     * 
     * @param namespace
     * @return
     * @throws FisException
     */
    private static boolean register(String namespace) throws FisException {
        return register(FisResource.DEFAULT_MAPPING_FILE_DIR, namespace);
    }

    /**
     * 注册命名空间，并加载对应的map.json文件
     * 
     * @param namespace
     * @return
     * @throws FisException
     */
    private static boolean register(String resMappingFileDir, String namespace) throws FisException {
        String mappingFileName = "map.json";
        if (namespace != FisResource.DEFAULT_NS_GLOBAL) {
            mappingFileName = namespace + "-map.json";
        }

        String mappingFileFullPath = resMappingFileDir + "/" + mappingFileName;

        // 加载配置数据
        BufferedReader br = null;
        try {
            String fileName = FisResource.class.getClassLoader().getResource(mappingFileFullPath).getPath();
            File mapJsonFile = new File(fileName);
            if (null == lastModifiedTimes.get(fileName)
                    || lastModifiedTimes.get(fileName) != mapJsonFile.lastModified()) {

                ObjectMapper mapper = new ObjectMapper(); 
                                                          
                resMap.put(namespace, mapper.readValue(mapJsonFile, HashMap.class));

                // 重置lastModifiedTime
                lastModifiedTimes.put(fileName, mapJsonFile.lastModified());
            }
            return true;
        } catch (Exception e) {
            throw new FisException("加载map json文件数据出错！", e);
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                }
            }
        }

    }

    private static void registerAll(String resMappingFileDir) throws FisException {
        String configDirPath = FisResource.class.getClassLoader().getResource(resMappingFileDir).getFile();

        File mapJsonDir = new File(configDirPath);
        String[] mapJsonFiles = mapJsonDir.list();

        FilenameFilter defaultFileNameFilter = new FilenameFilter() {
            public boolean accept(File dir, String name) {
                boolean isMatch = name.equals(DEFAULT_RES_MAPPING_FILE);
                return isMatch;

            }
        };
        FilenameFilter nameSpaceFileNameFilter = new FilenameFilter() {
            public boolean accept(File dir, String name) {
                boolean isMatch = name.endsWith("-" + DEFAULT_RES_MAPPING_FILE);
                return isMatch;

            }
        };

        String[] childrenFileNames = mapJsonDir.list(defaultFileNameFilter);
        String[] nameSpacechildrenFileNames = mapJsonDir.list(nameSpaceFileNameFilter);

        if ((null == childrenFileNames || 0 == childrenFileNames.length)
                && (null == nameSpacechildrenFileNames || 0 == nameSpacechildrenFileNames.length)) {
            throw new FisException("Resource Mapping File Not Found!");
        }

        // 注册缺省的映射文件
        if (null != childrenFileNames || 0 < childrenFileNames.length) {
            for (int i = 0; i < childrenFileNames.length; i++) {
                FisResource.register(FisResource.DEFAULT_MAPPING_FILE_DIR, FisResource.DEFAULT_NS_GLOBAL);
            }
        }

        // 注册包含命名空间的
        if (null != nameSpacechildrenFileNames || 0 < nameSpacechildrenFileNames.length) {
            for (int i = 0; i < nameSpacechildrenFileNames.length; i++) {
                String fileName = nameSpacechildrenFileNames[i];
                int pos = fileName.indexOf("-" + FisResource.DEFAULT_RES_MAPPING_FILE);
                String namespace = fileName.substring(0, pos);
                FisResource.register(FisResource.DEFAULT_MAPPING_FILE_DIR, namespace);
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
    
    
    
    /**
     * 获取静态资源URI,有包的时候，返回包的uri,从map.json资源文件中
     * 
     * @param key
     *            - 资源key,例如static/css/blog.css
     * @return String
     * @throws FisException
     */
    private static String getResourceUri(String name, Map<String, Map> nsResMap, Map<String, Object> res) throws FisException{
    	String uri = "";
		if(res == null || res.isEmpty()){
			throw new FisException("undefined resource  \"" +  name);
		}
		if(res.containsKey("pkg")){
			Map pkg = (Map) nsResMap.get("pkg").get(res.get("pkg"));
			uri = (String) pkg.get("uri");
		} else {
			uri = (String) res.get("uri");
		}

    	return uri;
    }
    
    /**
     * 根据name获取对应的资源json对象。
     * 
     * @param key
     *            资源key,例如static/css/blog.css
     * @return String
     * @throws FisException
     */
    private static Map<String, Object> getRes(Map<String, Map>nsResMap, String name) throws FisException{
    	Map<String, Object> res = (Map<String, Object>) nsResMap.get("res").get(name);
    	return res;
    }
    
    /**
     * 获取资源所属命名空间的资源数据
     * 
     * @param key
     *            - 资源key,例如static/css/blog.css
     * @return String
     * @throws FisException
     */
    private static Map<String, Map> getNsResMap(String name) throws FisException{
    	Map<String, Object> res = null;
    	int pos = name.indexOf(":");
    	String namespace = FisResource.DEFAULT_NS_GLOBAL;
    	if(name.indexOf(":") >= 0) {
    		namespace = name.substring(0, pos);
    	}
    	
    	if(!(resMap.containsKey(namespace) || register(namespace))){
    		throw new FisException("missing map file of \"" +  namespace);
    	}
    	
		Map<String, Map> nsResMap = resMap.get(namespace);
    	
    	return nsResMap;
    }
    
    

    /**
     * 获取静态资源URI,从map.json资源文件中
     * 
     * @param name
     *            - 资源key,例如static/css/blog.css
     * @param async 是否异步加载
     * @return String
     * @throws FisException
     */
    private String load(String name, boolean async) throws FisException{
        String loadedURI = this.resLoadedMap.get(name);
        if(null != loadedURI && "" != loadedURI){
        	if(!async && (this.requireAsyncCollection.get("res") != null && this.requireAsyncCollection.get("res").containsKey(name))){
        		this.delAsyncDeps(name);
        	}
        	return loadedURI;
        }

    	Map<String, Map> nsResMap = this.getNsResMap(name);
		Map<String, Object> res = this.getRes(nsResMap,name);
		if(res == null || res.isEmpty()){
			throw new FisException("undefined resource  \"" +  name);
		}
		
		
		Map pkg = null;
		Map pkgHas = new HashMap();
		String uri = this.getResourceUri(name, nsResMap, res);
		if(res.containsKey("pkg")){
			pkg = (Map) nsResMap.get("pkg").get(res.get("pkg"));
			List<String> has = (List<String>) pkg.get("has"); 
			for(int i = 0, len = has.size(); i < len; i++){
				String resId = has.get(i);
				this.resLoadedMap.put(resId, uri);
				pkgHas = (Map<String, Object>) nsResMap.get("res").get(resId); 
				if(!pkgHas.isEmpty()){
					this.loadDeps(pkgHas, async);
				}
			}
			
		} else {
			if(res.containsKey("deps")){
				List<String> deps = (List<String>)res.get("deps");
				for(int i = 0, len = deps.size(); i < len; i++){
					load(deps.get(i));
				}
			}
			this.resLoadedMap.put(name, uri);
			this.loadDeps(res, async);
		}
		
		if(async && res.get("type").equals("js")){
			if(this.requireAsyncCollection.get("res") == null){
				this.requireAsyncCollection.put("res", new HashMap<String, Object>());
			}
			if(pkg != null && !pkg.isEmpty()){
				if(this.requireAsyncCollection.get("pkg") == null){
					this.requireAsyncCollection.put("pkg", new HashMap<String, Object>());
				}
				this.requireAsyncCollection.get("pkg").put((String)res.get("pkg"), pkg);
				this.requireAsyncCollection.get("res").putAll(pkgHas);
			} else {
				this.requireAsyncCollection.get("res").put(name, res);
			}
		} else {
			if(null == this.staticResourceMap.get(res.get("type"))){
				this.staticResourceMap.put((String) res.get("type"), new ArrayList<String>());
			}
			this.staticResourceMap.get(res.get("type")).add(uri);
		}

		loadedURI = uri;
		return loadedURI;     
    }
    

    
    /**
     * 分析组件依赖
     * @param array res  组件信息
     * @param boolean async   是否异步
     * @throws FISException 
     */
    private void loadDeps(Map<String, Object> res, boolean async) throws FisException{
    	if(res.containsKey("extras") && ((Map<String, Object>)res.get("extras")).containsKey("async")){
    		List<String> asyncUris = (List<String>)((Map<String, Object>)res.get("extras")).get("async");
    		for(int i = 0, len = asyncUris.size(); i < len; i++){
    			this.load(asyncUris.get(i), true);
    		}
    	}
    	if(res.containsKey("deps")){
    		List<String> deps = (List<String>)res.get("deps");
    		for(int i = 0, len = deps.size(); i < len; i++){
    			this.load(deps.get(i), async);
    		}
    	}
    }
    
    /**
     * 删除指定异步资源及相关依赖资源
     * @param name
     */
    private void delAsyncDeps(String name){
    	Map<String,Object> res = (Map<String,Object>)this.requireAsyncCollection.get("res").get(name);
    	if(res.containsKey("pkg")){
    		Map<String, Object> pkg = (Map<String, Object>)this.requireAsyncCollection.get("pkg").get(res.get("pkg"));
    		if(!pkg.isEmpty()){
    			this.staticResourceMap.get("js").add((String)pkg.get("uri"));
    			this.requireAsyncCollection.get("pkg").remove(res.get("pkg"));
    			List<String> has = (List<String>)pkg.get("has");
    			for(int i = 0, len = has.size(); i < len; i++){
    				if(this.requireAsyncCollection.get("res").containsKey(has.get(i))){
    					this.delAsyncDeps(has.get(i));
    				}
    			}
    		}
    	} else {
    		this.staticResourceMap.get("js").add((String)((Map<String, Object>)this.requireAsyncCollection.get("res").get(name)).get("uri"));
    		this.requireAsyncCollection.get("res").remove(name);
    	}
    	
    	if(res.containsKey("deps")){
    		List<String> deps = (List<String>)res.get("deps");
    		for(int i = 0, len = deps.size(); i < len; i++){
    			String depsUri = deps.get(i);
    			if(this.requireAsyncCollection.get("res").containsKey(depsUri)){
    				this.delAsyncDeps(depsUri);
    			}
    		}
    	}
    }
    
    

}

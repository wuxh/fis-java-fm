package fis.front;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.*;

import org.codehaus.jackson.map.ObjectMapper;

public class FisDataUtil {
	
	public static Map<String, Object> getJSON(String jsonFile){
		Map<String, Object> data = new HashMap<String, Object>();
		BufferedReader br = null;
		try{
			String fileName = FisDataUtil.class
			.getClassLoader().getResource(jsonFile).getPath();
			File dataFile = new File(fileName);

			ObjectMapper mapper = new ObjectMapper(); 
			data.putAll(mapper.readValue(dataFile, HashMap.class));
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
				}
			}
		}
		return data;
	}
}

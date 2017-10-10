package es.uvigo.esei.dai.hybridserver.http;

import java.util.LinkedHashMap;
import java.util.Map;

public class HTTPParser {
	
	public static HTTPRequestMethod parseMethod(String method){
		switch (method.toUpperCase()) {
		case "HEAD":
			return HTTPRequestMethod.HEAD;

		case "GET":
			return HTTPRequestMethod.GET;

		case "POST":
			return HTTPRequestMethod.POST;

		case "PUT":
			return HTTPRequestMethod.PUT;

		case "DELETE":
			return HTTPRequestMethod.DELETE;

		case "TRACE":
			return HTTPRequestMethod.TRACE;

		case "OPTIONS":
			return HTTPRequestMethod.OPTIONS;

		case "CONNECT":
			return HTTPRequestMethod.CONNECT;

		default:
			return null;
		}
	}
	
	public static String parseResourceName(String resourceChain){

		System.out.println(resourceChain);
		if(resourceChain.contains("\\?")){
			return resourceChain.split("\\?")[0].substring(1);
		}else{
			return resourceChain.substring(1);
		}				
	}
	
	public static String[] parseResourcePath(String resourceChain){
		String resName = HTTPParser.parseResourceName(resourceChain);
		
		String[] components = resName.split("/");
		
		return components;
	}
	
	public static Map<String, String> parseResourceParameters(){
		Map<String, String> toret = new LinkedHashMap<String, String>();
		
		return toret;
	}

}

package es.uvigo.esei.dai.hybridserver.helpers;

import java.util.Map;
import java.util.UUID;

import es.uvigo.esei.dai.hybridserver.http.HTTPResponse;

/*
 * HTML builder class. Takes care of building pretty HTML content for HTTP responses
 * instead of having this logic in controllers 
 */

public class HTMLHelper {
	private static final String HEADER = "<html>";
	private static final String FOOTER = "</html>";
	
	private static final String LIST_HEADER = "<h3><ul>";
	private static final String LIST_FOOTER = "</ul></h3>";
	
	private static final String ERROR_HEADER = "<h1 class=\"error\">";
	private static final String ERROR_FOOTER = "</h1>";
	
	private static final String STYLESHEET = "<style>.error{color:red; padding: 300px; }</style>";
	
	private static final String AUTHOR = "<h1>Hybrid Server</h1><h2>Adrian Simon Reboredo</h2><h2>Josue Pato Valcarcel</h2>";
	

	/*
	 * Pretty prints a list of resources
	 */
	public static HTTPResponse printList(HTTPResponse response, String resource,  Map<UUID, String> elements){
		String content = HEADER.concat(AUTHOR).concat(LIST_HEADER);
		
		for (Map.Entry<UUID, String> row : elements.entrySet()) {
			content = content.concat("<li>");
			content = content.concat("<a href=\""
					+ resource + "?uuid="
					+ row.getKey().toString() + "\">");
			content = content.concat(row.getKey().toString());
			content = content.concat("</a></li>");
		}
		
		content = content.concat(LIST_FOOTER).concat(FOOTER);
		
		response.setContent(content);
		return response;
	}
	
	public static HTTPResponse printError(HTTPResponse response){
		String content = HEADER.concat(STYLESHEET).concat(ERROR_HEADER);
		
		content = content.concat(
				response.getStatus()
				+ ": "
				+ response.getStatus().getStatus()
				);
		content = content.concat(ERROR_FOOTER).concat(FOOTER);
		
		response.setContent(content);
		return response;
	}
	
	public static HTTPResponse printError(HTTPResponse response, String message){
		String content = HEADER.concat(STYLESHEET).concat(ERROR_HEADER);
		content = content.concat(
				response.getStatus()
				+ ": "
				+ response.getStatus().getStatus()
				+ "<br>" +
				message
				);
		content = content.concat(ERROR_FOOTER).concat(FOOTER);
		response.setContent(content);
		return response;
	}
	
	public static HTTPResponse printPostSuccess(HTTPResponse response, String resource, String uuid){
		response.setContent("<a href=\"" + resource + "?uuid=" + uuid + "\">" + uuid
				+ "</a>");
		return response;
	}
	
}

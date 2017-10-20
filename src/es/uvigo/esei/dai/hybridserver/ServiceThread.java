package es.uvigo.esei.dai.hybridserver;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Map;
import java.util.UUID;

import es.uvigo.esei.dai.hybridserver.http.HTTPHeaders;
import es.uvigo.esei.dai.hybridserver.http.HTTPParseException;
import es.uvigo.esei.dai.hybridserver.http.HTTPRequest;
import es.uvigo.esei.dai.hybridserver.http.HTTPResponse;
import es.uvigo.esei.dai.hybridserver.http.HTTPResponseStatus;
import es.uvigo.esei.dai.hybridserver.http.MIME;

/**
 * 
 * @author Adrián Simón Reboredo & Josué Pato Valcárcel
 *
 */
public class ServiceThread implements Runnable {
	private Socket socket = null;
	private HTTPRequest request;
	private HTTPResponse response;

	public ServiceThread(Socket socket) {
		this.socket = socket;
		this.response = new HTTPResponse();
		this.response.setVersion(HTTPHeaders.HTTP_1_1.getHeader());
		//Default: internal server error
		this.response.setStatus(HTTPResponseStatus.S500);
	}

	@Override
	public void run() {
		try (BufferedReader buffer = new BufferedReader(new InputStreamReader(
				socket.getInputStream()))) {
			try {
				this.request = new HTTPRequest(buffer);
				switch (this.request.getMethod()) {
				case GET:
					this.prepareGet();
					break;
				case POST:
					this.preparePost();
					break;
				case DELETE:
					this.prepareDelete();
					break;
				default:
					//Unsupported
					response.setStatus(HTTPResponseStatus.S405);
					break;
				}
			} catch (HTTPParseException e) {
				//Bad request
				this.response.setStatus(HTTPResponseStatus.S400);
			} finally{
				this.respond();
			}
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
	}

	private void respond() {
		try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
				socket.getOutputStream()))) {
			response.print(writer);
		} catch (IOException e) {
			System.err.println("Could not write to socket: " + e.getMessage());
		}
	}

	private void prepareGet() {
		Map<String, String> resources = this.request.getResourceParameters();
		if(resources.isEmpty()){
			this.renderHomepage();
		}else{
			this.renderDocument();
		}
	}

	private void preparePost() {
		Map<String, String> resources = this.request.getResourceParameters();
		System.out.println(HybridServer.documentDAO.isAvaliable());
		//TODO Entrega 2: take file type into account for different doctypes
		if(resources.containsKey("html")){
			try {
				String uuid = HybridServer.documentDAO.create(resources.get("html"));
				this.response.setContent("<a href=\"html?uuid=" + uuid + "\">" + uuid + "</a>");
				//OK
				this.response.setStatus(HTTPResponseStatus.S200);
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println( e.getMessage());
				//Default response is already S500
			}
		}else{
			//Bad request
			this.response.setStatus(HTTPResponseStatus.S400);
		}
	}

	private void prepareDelete() {
		Map<String, String> resources = this.request.getResourceParameters();
		
		if(!resources.isEmpty()){
			String uuid = this.request.getResourceParameters().get("uuid");
			this.delete(uuid);
		}
	}
	
	
	/* VIEWS */
	/**
	 * Creates the html view of the main page/file list
	 */
	private void renderHomepage(){
		this.response.setStatus(HTTPResponseStatus.S200);
		this.response.putParameter(HTTPHeaders.CONTENT_TYPE.getHeader(), MIME.TEXT_HTML.getMime());
		String content = "<html><h1>Hybrid Server</h1><h2>Adrian Simon Reboredo</h2><h2>Josue Pato Valcarcel</h2>";
		content = content.concat("<h3>Database Connection Status:&nbsp;");
		if(HybridServer.documentDAO.isAvaliable()){
			content = content.concat("<span style=\"color: green;\">CONNECTED</span>");
		}else{
			content = content.concat("<span style=\"color: red;\">NOT CONNECTED</span>");
		}
		content = content.concat("</h3><ul>");
		
		try {
			Map<UUID,String> db = HybridServer.documentDAO.list();
			for (Map.Entry<UUID,String>  row : db.entrySet()) {
				content = content.concat("<li>");
				content = content.concat("<a href=\"" + this.request.getResourceName() + "?uuid=" + row.getKey().toString() + "\">");
				content = content.concat(row.getKey().toString());
				content = content.concat("</a></li>");
			}
		} catch (Exception e) {
			System.out.println("Exception: " + e.getMessage());
		}		
		content = content.concat("</ul>");
		content = content.concat("</html>");
		
		this.response.setContent(content);
	}
	
	/**
	 * Creates the html view of any individual document
	 */
	private void renderDocument(){
		//TODO Entrega 2: switch documentt tyoe
		this.response.setStatus(HTTPResponseStatus.S200);
		this.response.putParameter(HTTPHeaders.CONTENT_TYPE.getHeader(), MIME.TEXT_HTML.getMime());
		String uuid = this.request.getResourceParameters().get("uuid");
		

		try {
			if(HybridServer.documentDAO.read(uuid) != null){
				this.response.setContent(HybridServer.documentDAO.read(uuid));
				this.response.setStatus(HTTPResponseStatus.S200);
			}else{
				this.response.setStatus(HTTPResponseStatus.S404);
			}			
		} catch (Exception e) {
			this.response.setStatus(HTTPResponseStatus.S400);
		}
	}
	
	
	/* ACTIONS */
	
	private void delete(String uuid){
		try {
			if(HybridServer.documentDAO.delete(uuid)){
				this.response.setStatus(HTTPResponseStatus.S200);
			}else{
				this.response.setStatus(HTTPResponseStatus.S404);
			}
		} catch (Exception e) {
			this.response.setStatus(HTTPResponseStatus.S400);
			System.out.println("Delete failed");
		}
	}

}

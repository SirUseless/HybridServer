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
	
	public void run(){
		try (BufferedReader buffer = new BufferedReader(new InputStreamReader(
				socket.getInputStream()))) {
			try {
				this.request = new HTTPRequest(buffer);
				switch (this.request.getResourceName()) {
				case "":
					this.renderHomepage();
					break;
				case "html": 
					this.manageHTML();
					break;
				case "xml": 
					this.manageXML();
					break;
				case "xsd": 
					this.manageXSD();
					break;
				case "xslt": 
					this.manageXSLT();
					break;
				default:
					//Unsupported resource type
					response.setStatus(HTTPResponseStatus.S400);
					this.renderError();
					break;
				}
			} catch (HTTPParseException e) {
				//Bad request
				this.response.setStatus(HTTPResponseStatus.S400);
				this.renderError();
			} finally{
				this.respond();
			}
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
	}

	public void manageHTML() {
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
					this.renderError();
					break;
				}
	}

	private void manageXSLT() {
		// TODO Auto-generated method stub
		
	}

	private void manageXSD() {
		// TODO Auto-generated method stub
		
	}

	private void manageXML() {
		// TODO Auto-generated method stub
		
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
			this.renderList();
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
				this.renderError();
			}
		}else{
			//Bad request
			this.response.setStatus(HTTPResponseStatus.S400);
			this.renderError();
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
	private void renderList(){
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
		
		
		
		try{
			//Test if uuid is valid; should throw an exception if not
			UUID.fromString(uuid);
			
			if(HybridServer.documentDAO.read(uuid) != null){
				this.response.setContent(HybridServer.documentDAO.read(uuid));
				this.response.setStatus(HTTPResponseStatus.S200);
			}else{
				this.response.setStatus(HTTPResponseStatus.S404);
				this.renderError();
			}			
		} catch (Exception e) {
			this.response.setStatus(HTTPResponseStatus.S404);
			this.renderError();
		}
	}
	
	private void renderError(){
		this.response.putParameter(HTTPHeaders.CONTENT_TYPE.getHeader(), MIME.TEXT_HTML.getMime());
		
		String content = "<html><style>h1{color:red; padding: 300px; }</style><h1>"+ this.response.getStatus() + ": " + this.response.getStatus().getStatus() + "</h1></html>";
		
		this.response.setContent(content);		
	}
	
	/* ACTIONS */
	
	private void delete(String uuid){
		try {
			if(HybridServer.documentDAO.delete(uuid)){
				this.response.setStatus(HTTPResponseStatus.S200);
			}else{
				this.response.setStatus(HTTPResponseStatus.S404);
				this.renderError();
			}
		} catch (Exception e) {
			this.response.setStatus(HTTPResponseStatus.S400);
			this.renderError();
			System.out.println("Delete failed");
		}
	}

}

package es.uvigo.esei.dai.hybridserver;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

import es.uvigo.esei.dai.hybridserver.controller.HTMLController;
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
	private Configuration cfg = null;
	private Socket socket = null;
	private HTTPRequest request;
	private HTTPResponse response;

	public ServiceThread(Socket socket, Configuration configuration) {
		this.cfg = configuration;
		this.socket = socket;
		this.response = new HTTPResponse();
		this.response.setVersion(HTTPHeaders.HTTP_1_1.getHeader());
		//Default: internal server error
		this.response.setStatus(HTTPResponseStatus.S500);
		this.renderError("We are in trouble!");
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
					this.renderError("Unsupported resource type");
					break;
				}
			} catch (HTTPParseException e) {
				//Bad request
				this.response.setStatus(HTTPResponseStatus.S400);
				this.renderError("Incorrect request format");
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
	
	private void renderHomepage(){
		this.response.setStatus(HTTPResponseStatus.S200);
		this.response.putParameter(HTTPHeaders.CONTENT_TYPE.getHeader(), MIME.TEXT_HTML.getMime());
		String content = "<html><h1>Hybrid Server</h1><h2>Adrian Simon Reboredo</h2><h2>Josue Pato Valcarcel</h2>";		
		this.response.setContent(content);
	}
	
	@SuppressWarnings("unused")
	private void renderError(){
		this.response.putParameter(HTTPHeaders.CONTENT_TYPE.getHeader(), MIME.TEXT_HTML.getMime());
		
		String content = "<html><style>h1{color:red; padding: 300px; }</style><h1>"+ this.response.getStatus() + ": " + this.response.getStatus().getStatus() + "</h1></html>";
		
		this.response.setContent(content);		
	}
	
	private void renderError(String msg){
		this.response.putParameter(HTTPHeaders.CONTENT_TYPE.getHeader(), MIME.TEXT_HTML.getMime());
		
		String content = "<html><style>h1{color:red; padding: 300px; }</style><h1>"+ this.response.getStatus() + ": " + this.response.getStatus().getStatus() + "</h1><p>" +
				msg +
				"</p></html>";
		
		this.response.setContent(content);		
	}

	private void manageHTML() {
		try {
			HTMLController htmlController = new HTMLController(
					cfg.getDbURL(),
					cfg.getDbUser(),
					cfg.getDbPassword());
			
			switch (this.request.getMethod()) {
			case GET:
				if (this.request.getResourceParameters().isEmpty()) {
					this.response = htmlController.list(this.request, this.response);
				}else{
					this.response = htmlController.get(this.request, this.response);
				}
				break;
			case POST:
				this.response = htmlController.post(this.request, this.response);
				break;
			case DELETE:
				this.response = htmlController.delete(this.request, this.response);
				break;
			default:
				//Unsupported
				response.setStatus(HTTPResponseStatus.S405);
				this.renderError("Unsupported method");
				break;
			}
		} catch (ClassNotFoundException e) {
			this.renderError("Could not create resource controller");
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

}

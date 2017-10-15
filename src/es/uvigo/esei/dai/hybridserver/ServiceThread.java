package es.uvigo.esei.dai.hybridserver;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

import es.uvigo.esei.dai.hybridserver.http.HTTPHeaders;
import es.uvigo.esei.dai.hybridserver.http.HTTPParseException;
import es.uvigo.esei.dai.hybridserver.http.HTTPRequest;
import es.uvigo.esei.dai.hybridserver.http.HTTPResponse;
import es.uvigo.esei.dai.hybridserver.http.HTTPResponseStatus;

public class ServiceThread implements Runnable {
	private Socket socket = null;
	private HTTPRequest request;

	public ServiceThread(Socket socket) {
		this.socket = socket;
	}

	@Override
	public void run() {
//		this.parseRequest();
//		if(this.request != null){
//			this.testHomepage();
//		}else{
//			this.sendServerError();
//		}
		
		this.testHomepage();

	}
	
	private void parseRequest(){
		try(Socket socket = this.socket){
			BufferedReader buffer;
			try {
				buffer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				this.request = new HTTPRequest(buffer);
			} catch (IOException e) {
				System.err.println("IOError: " + e.getMessage());
				this.request = null;
			} catch(HTTPParseException e){
				System.err.println("Parse error: " + e.getMessage());
				this.request = null;
			}
		}catch(IOException e){
			System.out.println("IOError: " + e.getMessage());
			this.request = null;
		}
	}

	private void testHomepage() {
		System.out.println("Homepage test method");
		try(BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())) ) {
			
			HTTPResponse response = new HTTPResponse();
			response.setVersion(HTTPHeaders.HTTP_1_1.getHeader());
			response.setStatus(HTTPResponseStatus.S200);
			response.putParameter(HTTPHeaders.CONTENT_TYPE.getHeader(), "text/html");
			response.setContent("<html><h1>Hybrid Server</h1></html>");
			response.print(writer);
		} catch (IOException e) {
			System.out.println("Could not write to socket: " + e.getMessage());
		}
		System.out.println("Homepage test method: terminated succesfully");
	}
	
	private void sendServerError(){
		System.err.println("Reporting server error to client");
		try(BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())) ) {
			
			HTTPResponse response = new HTTPResponse();
			response.setVersion(HTTPHeaders.HTTP_1_1.getHeader());
			response.setStatus(HTTPResponseStatus.S500);
			response.print(writer);
		} catch (IOException e) {
			System.out.println("Could not write to socket: " + e.getMessage());
		}
	}

	@SuppressWarnings("unused")
	private void returnWelcome() {
//		try (BufferedReader reader = new BufferedReader( new InputStreamReader(socket.getInputStream()))){
//			
//			HTTPRequest request = new HTTPRequest(reader);
//			System.out.println("caca");
//			System.out.println(request.toString());
			
			try(BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())) ) {
				
				HTTPResponse response = new HTTPResponse();
				response.setVersion(HTTPHeaders.HTTP_1_1.getHeader());
				response.setStatus(HTTPResponseStatus.S200);
				response.setContent("<html><h1>Hybrid Server</h1></html>");
				response.print(writer);
			} catch (IOException e) {
				System.out.println("peta en escribir" + e.getMessage());
			}
//			
//		} catch (HTTPParseException | IOException e) {
//			System.err
//					.println("Failed to parse HTTPRequest: " + e.getMessage());
//		}

	}

}
